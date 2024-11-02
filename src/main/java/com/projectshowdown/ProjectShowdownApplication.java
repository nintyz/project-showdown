package com.projectshowdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ProjectShowdownApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load(); // This loads variables from the .env file
        System.setProperty("GOOGLE_CONFIG_PATH", dotenv.get("GOOGLE_CONFIG_PATH"));
        SpringApplication.run(ProjectShowdownApplication.class, args);
    }

}
