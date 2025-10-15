package com.videogamedb.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionUtilTest {

    private EncryptionUtil encryptionUtil;

    @BeforeEach
    void setUp() {
        // Generate a test key (32 bytes base64 encoded)
        String testKey = "dGVzdC1lbmNyeXB0aW9uLWtleS0zMi1ieXRlcw=="; // "test-encryption-key-32-bytes" in base64
        encryptionUtil = new EncryptionUtil(testKey);
    }

    @Test
    void testEncryptAndDecrypt() {
        String plaintext = "john.doe@example.com";

        // Encrypt
        String encrypted = encryptionUtil.encryptField(plaintext);
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);

        // Decrypt
        String decrypted = encryptionUtil.decryptField(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptEmptyString() {
        String encrypted = encryptionUtil.encryptField("");
        assertEquals("", encrypted);
    }

    @Test
    void testDecryptEmptyString() {
        String decrypted = encryptionUtil.decryptField("");
        assertEquals("", decrypted);
    }

    @Test
    void testHashUsername() {
        String username = "john_doe";
        String hash1 = encryptionUtil.hashUsername(username);
        String hash2 = encryptionUtil.hashUsername(username);

        // Hash should be deterministic
        assertEquals(hash1, hash2);

        // Hash should be base64 encoded
        assertNotNull(hash1);
        assertTrue(hash1.length() > 0);
    }

    @Test
    void testMaskEmail() {
        String email = "john.doe@example.com";
        String masked = encryptionUtil.maskEmail(email);

        assertTrue(masked.startsWith("joh***@"));
        assertTrue(masked.contains("example.com"));
    }

    @Test
    void testMaskShortEmail() {
        String email = "ab@example.com";
        String masked = encryptionUtil.maskEmail(email);

        assertTrue(masked.contains("***@"));
        assertTrue(masked.contains("example.com"));
    }

    @Test
    void testGenerateSecureToken() {
        String token1 = encryptionUtil.generateSecureToken(16);
        String token2 = encryptionUtil.generateSecureToken(16);

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Should be random
    }

    @Test
    void testMultipleEncryptionsDifferent() {
        String plaintext = "sensitive-data";

        String encrypted1 = encryptionUtil.encryptField(plaintext);
        String encrypted2 = encryptionUtil.encryptField(plaintext);

        // Due to random IV, encryptions should be different
        assertNotEquals(encrypted1, encrypted2);

        // But both should decrypt to same value
        assertEquals(plaintext, encryptionUtil.decryptField(encrypted1));
        assertEquals(plaintext, encryptionUtil.decryptField(encrypted2));
    }
}
