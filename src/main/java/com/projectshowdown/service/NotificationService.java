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
     */
    public void notifyCustomMessage(String email, String subject, String header, String message) throws MessagingException {
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif; background-color: #f3eeea; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: auto; background-color: #f3eeea; padding: 20px; border-radius: 8px;\">"
                + "<h2 style=\"text-align: center; color: #333;\">" + header + "</h2>"
                + "<p style=\"text-align: center; color: #4b0082; font-size: 16px;\">" + message + "</p>"
                + "<div style=\"text-align: center; margin: 20px 0;\">"
                + "<img src=\"cid:showdown-logo\" alt=\"Showdown Logo\" style=\"width: 150px; height: auto;\">"
                + "</div>"
                + "<p style=\"text-align: center; color: #888; font-size: 12px;\">This is an auto-generated email. Please do not reply to this email.</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        System.out.println("HTML Message: " + htmlMessage);



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
        String message = "Dear " + playerName + ", your match will start in " + matchStartTime + " hour. Please be prepared and head to your assigned court.";

        notifyCustomMessage(email, subject, header, message);
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

        notifyCustomMessage(email, subject, header, message);
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

        notifyCustomMessage(email, subject, header, message);
    }
}
