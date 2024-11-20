package com.projectshowdown.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service class for sending emails using JavaMailSender.
 * Provides functionality to send HTML emails with inline images.
 */

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    /**
     * Sends an email to the specified recipient with the given subject and content.
     * The email is sent as HTML and includes an inline image for branding.
     *
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param text    The content of the email, which can include HTML formatting.
     * @throws MessagingException If there is an error creating or sending the email.
     */

    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        ClassPathResource imageResource = new ClassPathResource("static/ShowdownLogo.png");
        System.out.println("Sending email....");
        
        helper.setFrom("projectshowndown@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.addInline("showdown-logo.png", imageResource);
        
        emailSender.send(message);
    }

}