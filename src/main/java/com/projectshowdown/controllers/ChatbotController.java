package com.projectshowdown.controllers;

import com.projectshowdown.dto.ChatbotMessageDTO;
import com.projectshowdown.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling chatbot-related operations.
 * Exposes endpoints for interacting with the chatbot service.
 */
@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    
    // Injecting the ChatbotService
    @Autowired
    private ChatbotService chatbotService;

    /**
     * Endpoint to handle user input and retrieve the chatbot's response.
     *
     * @param messageRequest A {@link ChatbotMessageDTO} containing the user's message.
     * @return The chatbot's response as a {@link String}.
     * 
     * Example request payload:
     * <pre>
     * {
     *   "message": "Hello chatbot"
     * }
     * </pre>
     * 
     * Example response:
     * <pre>
     * "Hello! How can I help you today?"
     * </pre>
     */
    @PostMapping("/message")
    public String getChatbotResponse(@RequestBody ChatbotMessageDTO messageRequest) {
        return chatbotService.getResponse(messageRequest.getMessage());
    }    
}