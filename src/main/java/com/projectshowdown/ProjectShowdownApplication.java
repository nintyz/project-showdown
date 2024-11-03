package com.projectshowdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling // This enables scheduling throughout the application
public class ProjectShowdownApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load(); // This loads variables from the .env file
        System.setProperty("GOOGLE_CONFIG_PATH", dotenv.get("GOOGLE_CONFIG_PATH"));
        SpringApplication.run(ProjectShowdownApplication.class, args);
    }

}
