package com.projectshowdown.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    // Path to your key, which is a JSON file.
    String pathToKey = "C:\\Users\\coben\\OneDrive - Singapore Management University\\Uni - Year 2 Sem 1\\CS203 Collaborative Software Development\\Project\\firebaseKey.json";

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        FileInputStream serviceAccount =
            new FileInputStream(pathToKey); 

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                //.setDatabaseUrl(pathToFirebase) 
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
