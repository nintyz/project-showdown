package com.projectshowdown.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Service class for handling Two-Factor Authentication (2FA) functionality.
 * Provides methods for generating secret keys, QR codes, and verifying 2FA codes.
 */
@Service
public class TwoFactorAuthService {

    public static final String PROJECT_TITLE = "ProjectShowdown";

    /**
     * Generates a secret key for use in Two-Factor Authentication (2FA).
     *
     * @return A randomly generated secret key as a {@link String}.
     */

    public String generateSecretKey() {
        return new DefaultSecretGenerator().generate();
    }

    /**
     * Generates a QR Code URI based on the provided secret key.
     * This URI can be used to generate a QR Code image that users can scan
     * with their authentication app (e.g., Google Authenticator).
     *
     * @param secret The secret key to include in the QR Code.
     * @return A URI string that represents the QR Code data.
     */

    public String generateQrCodeImageUri(String secret) {
        String issuer = PROJECT_TITLE;

        QrData data = new QrData.Builder()
                .label(issuer)
                .secret(secret)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6) // 6-digit code
                .period(30) // 30-second period
                .build();

        return data.getUri();
    }

    /**
     * Generates a QR Code image in Base64 format.
     * The QR Code can be displayed to the user for scanning with an authentication app.
     *
     * @param qrCodeUri The URI containing the QR Code data.
     * @return A Base64-encoded string representing the QR Code image in PNG format.
     * @throws WriterException If an error occurs while generating the QR Code.
     * @throws IOException      If an error occurs while writing the image to a stream.
     */
    public String generateQrCodeImage(String qrCodeUri) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUri, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(pngData);
    }

    /**
     * Verifies the provided 2FA code against the secret key.
     * This method ensures that the 2FA code is valid within a time-synchronized window.
     *
     * @param secret The secret key associated with the user.
     * @param code   The 2FA code entered by the user.
     * @return {@code true} if the code is valid; {@code false} otherwise.
     */
    public boolean verifyCode(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setAllowedTimePeriodDiscrepancy(1);

        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        return codeVerifier.isValidCode(secret, code);
    }
}