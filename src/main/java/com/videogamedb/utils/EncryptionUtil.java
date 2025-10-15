package com.videogamedb.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM field-level encryption utility.
 * Compatible with Python Fernet for basic encryption/decryption scenarios.
 */
@Slf4j
@Component
public class EncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 128; // 128 bits
    private static final int AES_KEY_SIZE = 256;

    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    public EncryptionUtil(@Value("${app.encryption.key}") String encryptionKey) {
        if (encryptionKey == null || encryptionKey.isEmpty()) {
            throw new IllegalStateException("ENCRYPTION_KEY is not set. Application cannot start without encryption key.");
        }

        try {
            // Decode base64 key or use it directly
            byte[] keyBytes;
            if (encryptionKey.length() == 44 || encryptionKey.length() == 43) {
                // Likely base64 encoded (32 bytes = 43-44 chars in base64)
                keyBytes = Base64.getDecoder().decode(encryptionKey);
            } else {
                // Use SHA-256 to derive a 32-byte key
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(encryptionKey.getBytes(StandardCharsets.UTF_8));
            }

            if (keyBytes.length != 32) {
                throw new IllegalArgumentException("Encryption key must be 32 bytes (256 bits)");
            }

            this.secretKey = new SecretKeySpec(keyBytes, "AES");
            this.secureRandom = new SecureRandom();
            log.info("✅ Encryption utility initialized successfully");
        } catch (Exception e) {
            log.error("❌ Failed to initialize encryption utility", e);
            throw new RuntimeException("Failed to initialize encryption", e);
        }
    }

    /**
     * Encrypts a plaintext string using AES-GCM.
     * Returns Base64-encoded string in format: Base64(IV || ciphertext || tag)
     */
    public String encryptField(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return "";
        }

        try {
            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            // Encrypt
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV + ciphertext (ciphertext already includes auth tag in GCM)
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            // Return as Base64
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("❌ Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypts a Base64-encoded token.
     * Expects format: Base64(IV || ciphertext || tag)
     */
    public String decryptField(String token) {
        if (token == null || token.isEmpty()) {
            return "";
        }

        try {
            // Decode from Base64
            byte[] decodedBytes = Base64.getDecoder().decode(token);

            // Extract IV and ciphertext
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] ciphertext = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertext);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            // Decrypt
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("❌ Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Creates a masked version of an email address.
     * Example: john.doe@example.com -> joh***@example.com
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***.***";
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "***@***.***";
        }

        String localPart = parts[0];
        String domain = parts[1];

        String maskedLocal = localPart.length() > 3 
            ? localPart.substring(0, 3) + "***" 
            : "***";

        return maskedLocal + "@" + domain;
    }

    /**
     * Generates a cryptographically secure random token.
     */
    public String generateSecureToken(int byteLength) {
        byte[] tokenBytes = new byte[byteLength];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Hashes a username using SHA-256 for deterministic lookup.
     * Returns Base64-encoded hash.
     */
    public String hashUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(username.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("❌ Username hashing failed", e);
            throw new RuntimeException("Username hashing failed", e);
        }
    }
}
