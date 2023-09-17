package com.export.yona.api;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class PropertyFileController {
    @GetMapping("/prjNameConfig")
    public String prjNameConfig(){
        Properties properties = new Properties();
        InputStream input = ApiController.class.getResourceAsStream("/application.properties");
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String projectNameList = properties.getProperty("yona-project-name");

        return projectNameList;
    }

    @GetMapping("/updatePrjNameConfig")
    public String updateYonaProjectNameList(@RequestParam String projectNames){
        Properties properties = new Properties();
        InputStream input = ApiController.class.getResourceAsStream("/application.properties");
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(!projectNames.isEmpty()){
            properties.setProperty("yona-project-name", projectNames);
            try (OutputStream output = Files.newOutputStream(Paths.get("src/main/resources/application.properties"))) {
                properties.store(output, null);
            } catch (IOException e) {
                log.error("Failed to set properties");
            }
        }
        return "SUCCESS";
    }
}
