package com.videogamedb.services;

import com.videogamedb.dtos.requests.RegisterRequest;
import com.videogamedb.models.User;
import com.videogamedb.repositories.UserRepository;
import com.videogamedb.utils.EncryptionUtil;
import com.videogamedb.utils.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EncryptionUtil encryptionUtil;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
            "john_doe",
            "john@example.com",
            "SecurePassword123!",
            "John",
            "Doe",
            List.of()
        );

        mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsernameEnc("encrypted_username");
        mockUser.setUsernameHash("username_hash");
        mockUser.setEmailEnc("encrypted_email");
        mockUser.setPasswordHash("password_hash");
        mockUser.setPasswordSalt("password_salt");
        mockUser.setPasswordIterations(310000);
    }

    @Test
    void testRegisterUserSuccess() {
        // Setup mocks
        when(encryptionUtil.hashUsername(anyString())).thenReturn("username_hash");
        when(userRepository.existsByUsernameHash(anyString())).thenReturn(false);
        when(encryptionUtil.encryptField(anyString())).thenReturn("encrypted_field");
        when(userRepository.existsByEmailEnc(anyString())).thenReturn(false);
        when(encryptionUtil.maskEmail(anyString())).thenReturn("joh***@example.com");
        when(encryptionUtil.generateSecureToken(anyInt())).thenReturn("secure_token");

        PasswordService.HashedPassword hashedPassword = 
            new PasswordService.HashedPassword("salt", "hash", 310000);
        when(passwordService.hashPassword(anyString())).thenReturn(hashedPassword);

        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User user = i.getArgument(0);
            user.setId("new_user_id");
            return user;
        });

        // Execute
        User result = userService.registerUser(registerRequest);

        // Verify
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(encryptionUtil, times(1)).hashUsername(registerRequest.getUsername());
        verify(passwordService, times(1)).hashPassword(registerRequest.getPassword());
    }

    @Test
    void testRegisterUserDuplicateUsername() {
        when(encryptionUtil.hashUsername(anyString())).thenReturn("username_hash");
        when(userRepository.existsByUsernameHash(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(registerRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticateUserSuccess() {
        when(encryptionUtil.hashUsername(anyString())).thenReturn("username_hash");
        when(userRepository.findByUsernameHash(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordService.verifyPassword(anyString(), anyString(), anyString(), anyInt()))
            .thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        Optional<User> result = userService.authenticateUser("john_doe", "SecurePassword123!");

        assertTrue(result.isPresent());
        verify(passwordService, times(1)).verifyPassword(anyString(), anyString(), anyString(), anyInt());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAuthenticateUserInvalidPassword() {
        when(encryptionUtil.hashUsername(anyString())).thenReturn("username_hash");
        when(userRepository.findByUsernameHash(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordService.verifyPassword(anyString(), anyString(), anyString(), anyInt()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        Optional<User> result = userService.authenticateUser("john_doe", "WrongPassword");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).save(any(User.class)); // Save for failed attempt counter
    }

    @Test
    void testAuthenticateUserNotFound() {
        when(encryptionUtil.hashUsername(anyString())).thenReturn("username_hash");
        when(userRepository.findByUsernameHash(anyString())).thenReturn(Optional.empty());

        Optional<User> result = userService.authenticateUser("nonexistent", "password");

        assertFalse(result.isPresent());
        verify(passwordService, never()).verifyPassword(anyString(), anyString(), anyString(), anyInt());
    }
}
