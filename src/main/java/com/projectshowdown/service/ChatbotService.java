package com.projectshowdown.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectshowdown.configs.GoogleServiceConfig;

/**
 * Service class for interacting with the Dialogflow chatbot API.
 * This service sends user input to the Dialogflow agent and retrieves the chatbot's response.
 */
@Service
public class ChatbotService {

    private static final String PROJECT_ID = "projectshowdown-df5f2";
    private static final String LANGUAGE_CODE = "en";

    @Autowired
    private GoogleServiceConfig googleServiceConfig;

    /**
     * Sends user input to the Dialogflow API and retrieves the chatbot's response.
     *
     * @param userInput The user's input as a {@link String}.
     * @return The chatbot's response as a {@link String}. If an error occurs, returns an error message.
     */

    public String getResponse(String userInput) {
        String SESSION_ID = "1234";
        String url = String.format("https://dialogflow.googleapis.com/v2/projects/%s/agent/sessions/%s:detectIntent", PROJECT_ID, SESSION_ID);
        
        // Create request JSON body
        String requestBody = String.format(
            "{ \"queryInput\": { \"text\": { \"text\": \"%s\", \"languageCode\": \"%s\" } } }",
            userInput, LANGUAGE_CODE
        );

        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();

        try {
            String accessToken = googleServiceConfig.getAccessToken();
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Assuming the response is in JSON format, you might want to parse it to extract the actual message
            return response.body(); // Modify this to extract the relevant information if needed

        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing request"; // Return an error message instead of null
        }
    }
}
