package com.projectshowdown.configs;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Configuration
public class GoogleServiceConfig {

    @Value("${google.dialogflow.scope}")
    private String DIALOGFLOW_SCOPE;

    // Path to your key, which is a JSON file.
    @Value("${google.config.path}")
    private String pathToKey;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream(pathToKey);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("projectshowdown-df5f2.firebasestorage.app")
                //.setDatabaseUrl(pathToFirebase)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    // Method to get Google Cloud API access token (for Dialogflow or other services)
    @Bean
    public String getAccessToken() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(pathToKey);

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(serviceAccount)
                .createScoped(Collections.singleton(DIALOGFLOW_SCOPE));

        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();

        return token.getTokenValue();
    }
}
