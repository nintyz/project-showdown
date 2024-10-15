package com.projectshowdown.service;

import com.google.zxing.WriterException;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TwoFactorAuthServiceTest {

    @InjectMocks
    private TwoFactorAuthService twoFactorAuthService;

    @Mock
    private DefaultSecretGenerator secretGenerator;

    @Mock
    private CodeVerifier codeVerifier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        twoFactorAuthService = new TwoFactorAuthService();
    }

    @Test
    void generateSecretKey_ShouldReturnNonEmptyString() {
        String secretKey = twoFactorAuthService.generateSecretKey();
        assertNotNull(secretKey);
        assertFalse(secretKey.isEmpty());
    }

    @Test
    void generateQrCodeImageUri_ShouldReturnValidUri() {
        String secret = "TESTSECRET";
        String qrCodeUri = twoFactorAuthService.generateQrCodeImageUri(secret);
        assertNotNull(qrCodeUri);
        assertTrue(qrCodeUri.startsWith("otpauth://totp/ProjectShowdown?secret="));
        assertTrue(qrCodeUri.contains(secret));
    }

    @Test
    void generateQrCodeImage_ShouldReturnBase64EncodedImage() throws WriterException, IOException {
        String qrCodeUri = "otpauth://totp/Test:test@example.com?secret=TESTSECRET&issuer=Test";
        String base64Image = twoFactorAuthService.generateQrCodeImage(qrCodeUri);
        assertNotNull(base64Image);
        assertFalse(base64Image.isEmpty());
        assertTrue(base64Image.startsWith("iVBORw0KGgo")); // Common prefix for PNG in Base64
    }

    @Test
    void verifyCode_WithValidCode_ShouldReturnTrue() {
        String secret = "TESTSECRET";
        String validCode = "123456";

        // Create a spy of the TwoFactorAuthService
        TwoFactorAuthService spyService = spy(twoFactorAuthService);

        // Mock the behavior of the internal CodeVerifier
        doReturn(true).when(spyService).verifyCode(secret, validCode);

        boolean result = spyService.verifyCode(secret, validCode);
        assertTrue(result);

        // Verify that the method was called with the correct parameters
        verify(spyService).verifyCode(secret, validCode);
    }

    @Test
    void verifyCode_WithInvalidCode_ShouldReturnFalse() {
        String secret = "TESTSECRET";
        String invalidCode = "999999";

        // Create a spy of the TwoFactorAuthService
        TwoFactorAuthService spyService = spy(twoFactorAuthService);

        // Mock the behavior of the internal CodeVerifier
        doReturn(false).when(spyService).verifyCode(secret, invalidCode);

        boolean result = spyService.verifyCode(secret, invalidCode);
        assertFalse(result);

        // Verify that the method was called with the correct parameters
        verify(spyService).verifyCode(secret, invalidCode);
    }
}