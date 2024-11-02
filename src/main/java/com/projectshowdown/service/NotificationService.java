package com.projectshowdown.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class NotificationService {

    @Autowired
    private EmailService emailService;

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());

    /**
     * Notify a player with a custom HTML message.
     *
     * @param email Recipient's email.
     * @param subject Subject of the email.
     * @param header Main header text.
     * @param message Primary message text.
     * @param subHeader Sub-header text.
     * @param highlightedText Highlighted text to emphasize.
     */
    public void notifyCustomMessage(String email, String subject, String header, String message, String subHeader, String highlightedText) throws MessagingException {
        String htmlMessage = "<html>" 
                + "<body style=\"font-family: Arial, sans-serif;\">" 
                + "<div style=\"background-color: #f3eeea; padding: 20px;\">" 
                + "<h2 style=\"color: #333;\">" + header + "</h2>" 
                + "<p style=\"font-size: 16px;\">" + message + "</p>" 
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">" 
                + "<h3 style=\"color: #333;\">" + subHeader + "</h3>" 
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + highlightedText + "</p>" 
                + "</div>" 
                + "</div>" 
                + "</body>" 
                + "</html>";


        try {
            emailService.sendEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send notification to " + email, e);
            throw e;
        }
    }

    /**
     * Notify a player that their match is starting soon.
     *
     * @param email Recipient's email.
     * @param playerName Name of the player.
     * @param matchStartTime Time in minutes until the match starts.
     */
    public void notifyMatchStartingSoon(String email, String playerName, int matchStartTime) throws MessagingException {
        String subject = "Match Alert: Your match is starting soon!";
        String header = "Match Reminder";
        String message = "Dear " + playerName + ", your match will start in " + matchStartTime + " minutes. Please be prepared and head to your assigned court.";
        String subHeader = "Match Starting Soon";
        String highlightedText = "Be ready!";

        notifyCustomMessage(email, subject, header, message, subHeader, highlightedText);
    }

    /**
     * Notify players when a tournament is cancelled.
     *
     * @param email Recipient's email.
     * @param tournamentName Name of the tournament.
     */
    public void notifyTournamentCancelled(String email, String tournamentName) throws MessagingException {
        String subject = "Important: Tournament Cancellation";
        String header = "Tournament Cancelled";
        String message = "We regret to inform you that the tournament '" + tournamentName + "' has been cancelled due to unforeseen circumstances. We apologize for any inconvenience.";
        String subHeader = "Cancellation Notice";
        String highlightedText = "Thank you for your understanding.";

        notifyCustomMessage(email, subject, header, message, subHeader, highlightedText);
    }

    /**
     * Notify a player when they have been matched with another player for a tournament.
     *
     * @param email Recipient's email.
     * @param playerName Name of the player.
     * @param opponentName Name of the opponent.
     * @param tournamentName Name of the tournament.
     */
    public void notifyPlayerMatched(String email, String playerName, String opponentName, String tournamentName) throws MessagingException {
        String subject = "Match Notification: You've been paired for a tournament!";
        String header = "Match Assignment";
        String message = "Dear " + playerName + ", you have been matched with " + opponentName + " for the tournament '" + tournamentName + "'. Please check the schedule and prepare accordingly.";
        String subHeader = "Your Next Match";
        String highlightedText = "Good luck and play well!";

        notifyCustomMessage(email, subject, header, message, subHeader, highlightedText);
    }
}
