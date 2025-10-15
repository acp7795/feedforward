package com.videogamedb.services;

import com.videogamedb.dtos.requests.RegisterRequest;
import com.videogamedb.dtos.responses.UserResponse;
import com.videogamedb.models.User;
import com.videogamedb.repositories.UserRepository;
import com.videogamedb.utils.EncryptionUtil;
import com.videogamedb.utils.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;
    private final PasswordService passwordService;

    @Value("${app.security.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${app.security.account-lock-minutes:30}")
    private int accountLockMinutes;

    public User registerUser(RegisterRequest request) {
        // Check if username already exists
        String usernameHash = encryptionUtil.hashUsername(request.getUsername());
        if (userRepository.existsByUsernameHash(usernameHash)) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        String emailEnc = encryptionUtil.encryptField(request.getEmail());
        if (userRepository.existsByEmailEnc(emailEnc)) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Hash password
        PasswordService.HashedPassword hashedPassword = passwordService.hashPassword(request.getPassword());

        // Create user entity
        User user = new User();
        user.setUsernameEnc(encryptionUtil.encryptField(request.getUsername()));
        user.setUsernameHash(usernameHash);
        user.setEmailEnc(emailEnc);
        user.setEmailMasked(encryptionUtil.maskEmail(request.getEmail()));
        user.setFirstNameEnc(encryptionUtil.encryptField(request.getFirstName()));
        user.setLastNameEnc(encryptionUtil.encryptField(request.getLastName()));
        user.setPasswordHash(hashedPassword.getHashBase64());
        user.setPasswordSalt(hashedPassword.getSaltBase64());
        user.setPasswordIterations(hashedPassword.getIterations());
        user.setCreationDate(LocalDateTime.now());
        user.setRole("USER");
        user.setPlatforms(request.getPlatforms() != null ? request.getPlatforms() : List.of());
        user.setAuditToken(encryptionUtil.generateSecureToken(16));
        user.setFailedLoginAttempts(0);
        user.setMaxLoginAttempts(maxLoginAttempts);
        user.setAccountLockMinutes(accountLockMinutes);

        User savedUser = userRepository.save(user);
        log.info("✅ User registered successfully: {}", usernameHash);
        return savedUser;
    }

    public Optional<User> authenticateUser(String username, String password) {
        String usernameHash = encryptionUtil.hashUsername(username);
        Optional<User> userOpt = userRepository.findByUsernameHash(usernameHash);

        if (userOpt.isEmpty()) {
            log.warn("❌ Authentication failed: User not found");
            return Optional.empty();
        }

        User user = userOpt.get();

        // Check if account is locked
        if (user.getAccountLockedUntil() != null && 
            user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            log.warn("❌ Account locked until: {}", user.getAccountLockedUntil());
            throw new IllegalStateException("Account is locked. Please try again later.");
        }

        // Verify password
        boolean passwordValid = passwordService.verifyPassword(
            password,
            user.getPasswordSalt(),
            user.getPasswordHash(),
            user.getPasswordIterations()
        );

        if (!passwordValid) {
            // Increment failed attempts
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            // Lock account if max attempts reached
            if (user.getFailedLoginAttempts() >= user.getMaxLoginAttempts()) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(user.getAccountLockMinutes()));
                log.warn("❌ Account locked after {} failed attempts", user.getFailedLoginAttempts());
            }

            userRepository.save(user);
            log.warn("❌ Authentication failed: Invalid password");
            return Optional.empty();
        }

        // Reset failed attempts on successful login
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        log.info("✅ User authenticated successfully");
        return Optional.of(user);
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(encryptionUtil.decryptField(user.getUsernameEnc()))
            .email(encryptionUtil.decryptField(user.getEmailEnc()))
            .emailMasked(user.getEmailMasked())
            .firstName(encryptionUtil.decryptField(user.getFirstNameEnc()))
            .lastName(encryptionUtil.decryptField(user.getLastNameEnc()))
            .creationDate(user.getCreationDate())
            .role(user.getRole())
            .platforms(user.getPlatforms())
            .lastLoginDate(user.getLastLoginDate())
            .failedLoginAttempts(user.getFailedLoginAttempts())
            .accountLockedUntil(user.getAccountLockedUntil())
            .build();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
        log.info("✅ User deleted: {}", id);
    }
}
