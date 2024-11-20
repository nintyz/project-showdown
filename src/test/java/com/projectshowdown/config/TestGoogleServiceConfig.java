package com.projectshowdown.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;

@TestConfiguration
public class TestGoogleServiceConfig {

    @Value("${google.dialogflow.scope}")
    private String DIALOGFLOW_SCOPE;

    @Value("${google.credentials.json}")
    private String googleCredentialsJson;

    @Bean
    @Primary // This makes sure the test version of FirebaseApp is used in the context instead of the real one
    public FirebaseApp firebaseApp() {
        // Mock the FirebaseApp to prevent actual Firebase initialization during tests
        return Mockito.mock(FirebaseApp.class);
    }

}
