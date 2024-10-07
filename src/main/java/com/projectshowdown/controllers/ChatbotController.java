package com.projectshowdown.controllers;

import com.projectshowdown.services.ChatbotService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    
    // Injecting the ChatbotService
    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    // Endpoint to handle user input and get the chatbot response
    @PostMapping("/message")
    public String getChatbotResponse(@RequestBody MessageRequest messageRequest) {
        return chatbotService.getResponse(messageRequest.getMessage());
    }    
}

class MessageRequest {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
