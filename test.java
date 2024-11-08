public void notifyCustomMessage(String email, String subject, String header, String message) throws MessagingException {
    String htmlMessage = String.format("""
        <html>
        <body style="font-family: Arial, sans-serif; background-color: #f3eeea; padding: 20px;">
            <div style="max-width: 600px; margin: auto; background-color: #f3eeea; padding: 20px; border-radius: 8px;">
                <h2 style="text-align: center; color: #333;">%s</h2>
                <p style="text-align: center; color: #4b0082; font-size: 16px;">%s</p>
                <div style="text-align: center; margin: 20px 0;">
                    <img src="cid:showdown-logo" alt="Showdown Logo" style="width: 150px; height: auto;">
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