package com.projectshowdown.controllers;

import com.projectshowdown.service.NotificationService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/sendCustomMessage")
    public String sendCustomMessage(@RequestParam String email) {
        try {
            notificationService.notifyCustomMessage(
                    email,
                    "Test Notification",
                    "Test Header",
                    "This is a test message body"
            );
            return "Custom notification sent successfully!";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send custom notification.";
        }
    }

    @GetMapping("/matchReminder")
    public String sendMatchReminder(@RequestParam String email, @RequestParam String playerName) {
        try {
            notificationService.notifyMatchStartingSoon(email, playerName, 1); // 1 hour before the match
            return "Match reminder notification sent successfully!";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send match reminder.";
        }
    }

    @GetMapping("/tournamentCancelled")
    public String notifyTournamentCancelled(@RequestParam String email, @RequestParam String tournamentName) {
        try {
            notificationService.notifyTournamentCancelled(email, tournamentName);
            return "Tournament cancellation notification sent successfully!";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send tournament cancellation notification.";
        }
    }

    @GetMapping("/playerMatched")
    public String notifyPlayerMatched(@RequestParam String email, @RequestParam String playerName, @RequestParam String opponentName, @RequestParam String tournamentName) {
        try {
            notificationService.notifyPlayerMatched(email, playerName, opponentName, tournamentName);
            return "Player matched notification sent successfully!";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send player matched notification.";
        }
    }
}