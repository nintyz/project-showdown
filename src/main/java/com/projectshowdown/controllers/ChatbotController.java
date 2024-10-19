package com.projectshowdown.controllers;

import com.projectshowdown.service.ChatbotService;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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

@Setter
@Getter
class MessageRequest {
    private String message;

}
