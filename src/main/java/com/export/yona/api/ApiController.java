package com.export.yona.api;

import com.export.yona.domain.YonaApiModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Date;

@Slf4j
@Controller
public class ApiController {
    static Properties properties = new Properties();
    @GetMapping("/")
    public String homepage(Model model, @RequestParam(required = false, defaultValue = "test") String username) {
        return "redirect:/prjNameConfig";
    }

    /**
     * @throws IOException
     * @throws SQLException
     */
    @GetMapping("/updateBatchData")
    public String updateBatchData() throws IOException, SQLException {

        InputStream input = ApiController.class.getResourceAsStream("/application.properties");
        properties.load(input);
        String projectNameList = properties.getProperty("yona-project-name");
        String destinationDBUrl =  properties.getProperty("destination-db-url");
        String destinationDBId =  properties.getProperty("destination-db-id");
        String destinationDBPwd =  properties.getProperty("destination-db-pwd");
        String apiUrl = properties.getProperty("yona-api-url");
        String adminId = properties.getProperty("yona-admin-id");
        String userToken = properties.getProperty("yona-admin-userToken");

        int batchSize = 500; // INSERT 문 연결 1회에 실행하는 row 단위

        if (projectNameList != null) {
            String[] projectNames = projectNameList.split(",");

            for (String projectName : projectNames) {
                log.info("URL for project :" + projectName.trim());
                String urlStr = apiUrl + "/-_-api/v1/owners/" + adminId + "/projects/" + projectName.trim() + "/exports";

                log.info(urlStr);
                log.info("{} ,{}, {}", destinationDBUrl, destinationDBId , destinationDBPwd);

                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Yona-Token", userToken);

                    int responseCode = conn.getResponseCode();
                    if(responseCode == 200){
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        //log.info(response.toString());

                        String json = response.toString();
                        ObjectMapper mapper = new ObjectMapper();

                        YonaApiModel project = mapper.readValue(json, YonaApiModel.class);

                        log.info("ISSUES :"+ project.issues.get(0).toString());


                        // DELETE
                        deleteAllYonaData(properties);

                        // INSERT
                        try (Connection connDB = DriverManager.getConnection(destinationDBUrl, destinationDBId, destinationDBPwd)) {
                            connDB.setAutoCommit(false);

                            String sql = "INSERT INTO yona_issue_posts " +
                                    "(OWNER, PROJECT_NAME, TITLE, BODY, REG_DATE, MOD_DATE, TYPE, STATE, REF_URL, ARTICLE_ID_YONA, AUTHOR_NAME  ) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            PreparedStatement stmt = connDB.prepareStatement(sql);

                            int batchCount = 0;
                            for (int i = 0; i < project.issues.size(); i++) {
                                YonaApiModel.Issue issue = project.issues.get(i);
                                stmt.setString(1, issue.getOwner());
                                stmt.setString(2, issue.getProjectName());
                                stmt.setString(3, issue.getTitle());
                                stmt.setString(4, issue.getBody());
                                stmt.setTimestamp(5, issue.getCreatedAt());
                                stmt.setTimestamp(6, issue.getUpdatedAt());
                                stmt.setString(7,"YONA_"+issue.getType());
                                stmt.setString(8, issue.getState());
                                stmt.setString(9, issue.getRefUrl());
                                stmt.setInt(10, issue.getNumber());
                                stmt.setString(11, issue.getAuthor().getName());
                                stmt.addBatch();
                                // Execute the batch when it reaches the specified size or at the end of the data
                                if ((i + 1) % batchSize == 0 || i == project.issues.size() - 1) {
                                    batchCount+=1;
                                    log.info("INSERT Batch Iteration :" + batchCount);
                                    int[] batchResults = stmt.executeBatch();
                                    // Commit the transaction if all rows were inserted successfully
                                    connDB.commit();

                                    // Handle any batch insertion failures if necessary
                                    for (int result : batchResults) {
                                        if (result == PreparedStatement.EXECUTE_FAILED) {
                                            log.info("Batch Fail : " + result);
                                        }
                                    }
                                    stmt.clearBatch();
                                }
                            }
                        } catch (SQLException e) {
                            log.error("SQLException");
                            throw new RuntimeException(e);
                        }

                        // Close connections
                        in.close();
                    } else {
                        log.error("GET request not worked");
                    }
                } catch (IOException e) {
                    log.error("Error creating HTTP connection");
                    throw new RuntimeException("Error creating HTTP connection", e);
                }
            }
        } else {
            log.info("No project names found.");
        }
        log.info("INSERT SUCCESS");
        return "index";
    }
    public static void deleteAllYonaData(Properties properties){
        String destinationDBUrl =  properties.getProperty("destination-db-url");
        String destinationDBId =  properties.getProperty("destination-db-id");
        String destinationDBPwd =  properties.getProperty("destination-db-pwd");
        try (Connection connDB = DriverManager.getConnection(destinationDBUrl, destinationDBId, destinationDBPwd)) {
            String sql = "DELETE FROM yona_issue_posts where type='YONA_ISSUE_POST'";
            PreparedStatement stmt = connDB.prepareStatement(sql);
            stmt.executeUpdate();
            log.info("DELETE AllYonaData SUCCESS");
        }catch (Exception e){
            log.info("ERROR WHILE DELETE");
        }


    }
}
