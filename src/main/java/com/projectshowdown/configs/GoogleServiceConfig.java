package com.projectshowdown.configs;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Configuration
public class GoogleServiceConfig {

    private static final String CREDENTIALS_FILE_PATH = "C:\\Users\\Andre\\Desktop\\firebaseKey.json"; // Update this path
    private static final String DIALOGFLOW_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

    // Firebase initialization
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(CREDENTIALS_FILE_PATH);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    // Method to get Google Cloud API access token (for Dialogflow or other services)
    public String getAccessToken() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(CREDENTIALS_FILE_PATH);

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(serviceAccount)
                .createScoped(Collections.singleton(DIALOGFLOW_SCOPE));

        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();

        return token.getTokenValue();
    }
}
