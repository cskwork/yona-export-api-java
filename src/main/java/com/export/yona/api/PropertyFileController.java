package com.export.yona.api;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class PropertyFileController {
    @GetMapping("/prjNameConfig")
    public String prjNameConfig(Model model) throws IOException {
        Properties properties = new Properties();
        //InputStream input = ApiController.class.getResourceAsStream("/application.properties");
        InputStream input = Files.newInputStream(Paths.get("src/main/resources/application.properties"));
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String projectNameList = properties.getProperty("yona-project-name");
        List<String> projectNames = Arrays.asList(projectNameList.split("\\s*,\\s*"));

        model.addAttribute("projectNames", projectNames);
        return "index";
    }

    @PostMapping("/addPrjNameConfig")
    public String addPrjNameConfig(@RequestParam String projectName, Model model) throws IOException {
        log.info("Add PrjNameConfig : " + projectName );
        String projectNameList ="";
        Properties properties = new Properties();
        //InputStream input = ApiController.class.getResourceAsStream("/application.properties");
        InputStream input = Files.newInputStream(Paths.get("src/main/resources/application.properties"));
        try {
            properties.load(input);
            projectNameList = properties.getProperty("yona-project-name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Add projectNameList : " + projectNameList );

        if(!projectName.isEmpty()){
            properties.setProperty("yona-project-name", projectNameList+","+projectName);
            try (OutputStream output = Files.newOutputStream(Paths.get("src/main/resources/application.properties"))) {
                properties.store(output, null);
                //properties.load(input);
                //projectNameList = properties.getProperty("yona-project-name");
            } catch (IOException e) {
                log.error("Failed to set properties");
            }
        }
        model.addAttribute("projectName", projectName);
        model.addAttribute("projectNames", projectNameList);
        return "redirect:/prjNameConfig";
    }
}
