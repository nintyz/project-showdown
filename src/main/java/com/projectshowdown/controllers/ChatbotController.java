package com.projectshowdown.controllers;

import com.projectshowdown.dto.ChatbotMessageDTO;
import com.projectshowdown.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    
    // Injecting the ChatbotService
    @Autowired
    private ChatbotService chatbotService;

    // Endpoint to handle user input and get the chatbot response
    @PostMapping("/message")
    public String getChatbotResponse(@RequestBody ChatbotMessageDTO messageRequest) {
        return chatbotService.getResponse(messageRequest.getMessage());
    }    
}