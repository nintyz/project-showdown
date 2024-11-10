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
        String htmlMessage = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f3eeea; padding: 20px;">
                <div style="max-width: 800px; margin: auto; background-color: #f3eeea; padding: 20px; border-radius: 8px;">
                    <h2 style="text-align: center; color: #333;">%s</h2>
                    <p style="text-align: center; color: #333; font-size: 16px;">%s</p>
                    <div style="text-align: center; margin: 20px 0;">
                        <img src="cid:showdown-logo.png" alt="Showdown Logo" style="width: 400px; height: auto;">
                    </div>
                    <p style="text-align: center; color: #888; font-size: 12px;">This is an auto-generated email. Please do not reply to this email.</p>
                </div>
            </body>
            </html>
            """, header, message);

        System.out.println("HTML Message: " + htmlMessage);



        try {
            emailService.sendEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send notification to " + email, e);
            throw e;
        }
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
        String message = "We regret to inform you that the tournament '" + tournamentName + "' has been cancelled due to unforeseen circumstances. We apologise for any inconvenience.";

        notifyCustomMessage(email, subject, header, message);
    }

    /**
     * Notify a player when they have been matched with another player for a tournament but the details are still pending.
     *
     * @param email Recipient's email.
     * @param playerName Name of the player.
     * @param opponentName Name of the opponent.
     * @param tournamentName Name of the tournament.
     */
    public void notifyPlayerMatched(String email, String playerName, String opponentName, String tournamentName) throws MessagingException {
        String subject = "Match Notification: You've been paired for a tournament!";
        String header = "Match Assignment";
        String message = "Dear " + playerName + ", you have been matched with " + opponentName + " for the tournament '" + tournamentName + "'. The date, time, and venue are currently set to 'TBC'. Please stay tuned for updates.";

        notifyCustomMessage(email, subject, header, message);
    }

    /**
     * Notify a player when their match details have been updated.
     *
     * @param email Recipient's email.
     * @param playerName Name of the player.
     * @param opponentName Name of the opponent.
     * @param tournamentName Name of the tournament.
     * @param date Date of the tournament.
     * @param time Time of the opponent.
     */
    public void notifyMatchDetailsUpdated(String email, String playerName, String opponentName, String tournamentName, String date, String time) throws MessagingException {
        String subject = "Match Details Updated: Tournament Notification";
        String header = "Match Details Updated";
        String message = "Dear " + playerName + ", the match details for your upcoming game against " + opponentName +
        " in the tournament '" + tournamentName + "' have been updated. The match is scheduled for " +
        date + " at " + time + ". Please mark your calendar and prepare accordingly.";

        notifyCustomMessage(email, subject, header, message);
    }
}
