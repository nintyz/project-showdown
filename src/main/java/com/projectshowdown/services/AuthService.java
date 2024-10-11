package com.projectshowdown.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

public class AuthService {

    private static final String CREDENTIALS_FILE_PATH = "D:\\GitHub\\project-showdown\\src\\main\\resources\\firebaseKey.json"; // Update this path
    private static final String DIALOGFLOW_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

    public String getAccessToken() throws IOException {
        // Load the service account credentials
        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
            .createScoped(Collections.singleton(DIALOGFLOW_SCOPE));

        // Refresh the token (if necessary) and get the AccessToken
        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();

        // Return the token value as a string
        return token.getTokenValue();
    }
}
