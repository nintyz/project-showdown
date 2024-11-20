package com.projectshowdown.configs;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

/**
 * Configuration class for Google services, including Firebase and Dialogflow.
 */
@Configuration
public class GoogleServiceConfig {

    @Value("${google.dialogflow.scope}")
    private String DIALOGFLOW_SCOPE;

    @Value("${google.credentials.json}")
    private String googleCredentialsJson;

    /**
     * Initializes the Firebase application with the provided credentials.
     *
     * @return the initialized FirebaseApp instance
     * @throws IOException if an I/O error occurs
     */
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Create credentials from JSON string instead of file
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(googleCredentialsJson.getBytes())
        );

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    /**
     * Retrieves an access token for Google services with the specified scope.
     *
     * @return the access token as a String
     * @throws IOException if an I/O error occurs
     */
    @Bean
    public String getAccessToken() throws IOException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ByteArrayInputStream(googleCredentialsJson.getBytes()))
                .createScoped(Collections.singleton(DIALOGFLOW_SCOPE));

        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();

        return token.getTokenValue();
    }
}