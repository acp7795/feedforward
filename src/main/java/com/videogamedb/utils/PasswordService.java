package com.videogamedb.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * PBKDF2-HMAC-SHA256 password hashing service.
 * Compatible with Python's hashlib.pbkdf2_hmac implementation.
 */
@Slf4j
@Service
public class PasswordService {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits
    private static final int DERIVED_KEY_LENGTH = 32; // 32 bytes = 256 bits

    private final int defaultIterations;
    private final SecureRandom secureRandom;

    public PasswordService(@Value("${app.security.pbkdf2-iterations:310000}") int defaultIterations) {
        this.defaultIterations = defaultIterations;
        this.secureRandom = new SecureRandom();
        log.info("✅ Password service initialized with {} iterations", defaultIterations);
    }

    /**
     * Result of password hashing operation.
     */
    @Data
    @AllArgsConstructor
    public static class HashedPassword {
        private String saltBase64;
        private String hashBase64;
        private int iterations;
    }

    /**
     * Hashes a password using PBKDF2-HMAC-SHA256.
     * Generates a random salt and returns salt + hash as Base64.
     */
    public HashedPassword hashPassword(String password) {
        return hashPassword(password, defaultIterations);
    }

    /**
     * Hashes a password with specified iteration count.
     */
    public HashedPassword hashPassword(String password, int iterations) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            // Generate random salt
            byte[] salt = new byte[SALT_LENGTH];
            secureRandom.nextBytes(salt);

            // Hash password
            byte[] hash = pbkdf2(password, salt, iterations);

            return new HashedPassword(
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash),
                iterations
            );
        } catch (Exception e) {
            log.error("❌ Password hashing failed", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verifies a password against stored salt and hash.
     * Uses timing-safe comparison to prevent timing attacks.
     */
    public boolean verifyPassword(String password, String saltBase64, String hashBase64, int iterations) {
        if (password == null || saltBase64 == null || hashBase64 == null) {
            return false;
        }

        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            byte[] expectedHash = Base64.getDecoder().decode(hashBase64);
            byte[] actualHash = pbkdf2(password, salt, iterations);

            // Timing-safe comparison
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) {
            log.error("❌ Password verification failed", e);
            return false;
        }
    }

    /**
     * Performs PBKDF2-HMAC-SHA256 key derivation.
     */
    private byte[] pbkdf2(String password, byte[] salt, int iterations) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(),
            salt,
            iterations,
            DERIVED_KEY_LENGTH * 8 // Convert bytes to bits
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Generates a cryptographically secure random password.
     */
    public String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}
