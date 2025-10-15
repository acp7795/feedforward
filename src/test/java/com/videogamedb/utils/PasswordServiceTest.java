package com.videogamedb.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService(310000);
    }

    @Test
    void testHashPassword() {
        String password = "mySecurePassword123!";

        PasswordService.HashedPassword result = passwordService.hashPassword(password);

        assertNotNull(result);
        assertNotNull(result.getSaltBase64());
        assertNotNull(result.getHashBase64());
        assertEquals(310000, result.getIterations());
    }

    @Test
    void testVerifyPasswordSuccess() {
        String password = "mySecurePassword123!";

        PasswordService.HashedPassword hashed = passwordService.hashPassword(password);

        boolean verified = passwordService.verifyPassword(
            password,
            hashed.getSaltBase64(),
            hashed.getHashBase64(),
            hashed.getIterations()
        );

        assertTrue(verified);
    }

    @Test
    void testVerifyPasswordFailure() {
        String password = "mySecurePassword123!";
        String wrongPassword = "wrongPassword";

        PasswordService.HashedPassword hashed = passwordService.hashPassword(password);

        boolean verified = passwordService.verifyPassword(
            wrongPassword,
            hashed.getSaltBase64(),
            hashed.getHashBase64(),
            hashed.getIterations()
        );

        assertFalse(verified);
    }

    @Test
    void testDifferentSaltsProduceDifferentHashes() {
        String password = "mySecurePassword123!";

        PasswordService.HashedPassword hash1 = passwordService.hashPassword(password);
        PasswordService.HashedPassword hash2 = passwordService.hashPassword(password);

        // Salts should be different
        assertNotEquals(hash1.getSaltBase64(), hash2.getSaltBase64());

        // Hashes should be different
        assertNotEquals(hash1.getHashBase64(), hash2.getHashBase64());

        // But both should verify with their own salt
        assertTrue(passwordService.verifyPassword(
            password, hash1.getSaltBase64(), hash1.getHashBase64(), hash1.getIterations()
        ));
        assertTrue(passwordService.verifyPassword(
            password, hash2.getSaltBase64(), hash2.getHashBase64(), hash2.getIterations()
        ));
    }

    @Test
    void testGenerateRandomPassword() {
        String password1 = passwordService.generateRandomPassword(16);
        String password2 = passwordService.generateRandomPassword(16);

        assertEquals(16, password1.length());
        assertEquals(16, password2.length());
        assertNotEquals(password1, password2);
    }

    @Test
    void testVerifyPasswordWithNullValues() {
        assertFalse(passwordService.verifyPassword(null, "salt", "hash", 310000));
        assertFalse(passwordService.verifyPassword("password", null, "hash", 310000));
        assertFalse(passwordService.verifyPassword("password", "salt", null, 310000));
    }
}
