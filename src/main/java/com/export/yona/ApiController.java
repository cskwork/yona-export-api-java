package com.export.yona;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class ApiController {
    public static void main(String[] args) throws IOException, SQLException {
        Properties properties = new Properties();
        InputStream input = ApiController.class.getResourceAsStream("/application.properties");
        properties.load(input);

        String apiUrl = properties.getProperty("yona-api-url");
        String projectNameList = properties.getProperty("yona-project-name");
        String adminId = properties.getProperty("yona-admin-id");
        String userToken = properties.getProperty("yona-admin-userToken");
        String destinationDBUrl =  properties.getProperty("destination-db-url");
        String destinationDBId =  properties.getProperty("destination-db-id");
        String destinationDBPwd =  properties.getProperty("destination-db-pwd");
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

                        try (Connection connDB = DriverManager.getConnection(destinationDBUrl, destinationDBId, destinationDBPwd)) {
                            connDB.setAutoCommit(false);

                            String sql = "INSERT INTO yona (owner, projectName, projectDescription) VALUES (?, ?, ?)";
                            PreparedStatement stmt = connDB.prepareStatement(sql);

                            int batchCount = 0;
                            for (int i = 0; i < project.issues.size(); i++) {
                                YonaApiModel.Issue issue = project.issues.get(i);
                                stmt.setString(1, issue.getOwner());
                                stmt.setString(2, issue.getProjectName());
                                stmt.setString(3, issue.getTitle());
                                stmt.addBatch();
                                // Execute the batch when it reaches the specified size or at the end of the data
                                if ((i + 1) % batchSize == 0 || i == project.issues.size() - 1) {
                                    batchCount+=1;
                                    log.info("Batch Iteration :" + batchCount);
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

    }
}
