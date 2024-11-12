package com.projectshowdown.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.google.firebase.FirebaseApp;

@TestConfiguration
public class TestGoogleServiceConfig {

    @Bean
    @Primary // This makes sure the test version of FirebaseApp is used in the context instead of the real one
    public FirebaseApp firebaseApp() {
        // Mock the FirebaseApp to prevent actual Firebase initialization during tests
        return Mockito.mock(FirebaseApp.class);
    }
}
