package com.projectshowdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // This enables scheduling throughout the application
public class ProjectShowdownApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectShowdownApplication.class, args);
    }

}
