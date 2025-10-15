package com.videogamedb.controllers;

import com.videogamedb.dtos.requests.LoginRequest;
import com.videogamedb.dtos.requests.RegisterRequest;
import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.dtos.responses.UserResponse;
import com.videogamedb.models.AccessTime;
import com.videogamedb.models.User;
import com.videogamedb.repositories.AccessTimeRepository;
import com.videogamedb.services.UserService;
import com.videogamedb.utils.LogHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AccessTimeRepository accessTimeRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("📝 ENTER register(username: {})", request.getUsername());
        log.info(LogHelper.formatSensitiveData("Registration Password", request.getPassword()));
        log.info(LogHelper.formatSensitiveData("Registration Email", request.getEmail()));

        try {
            log.info("📝 Registration request for username: {}", request.getUsername());
            log.debug("🔐 Starting user registration process");

            User user = userService.registerUser(request);
            log.info("✅ User registered successfully: {}", user.getId());

            UserResponse response = userService.toUserResponse(user);
            log.debug("📝 Converted user to response DTO");

            log.info("📝 EXIT register() - success, user ID: {}", user.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", response));
        } catch (IllegalArgumentException e) {
            log.warn("❌ Registration failed: {}", e.getMessage());
            log.info("📝 EXIT register() - failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("❌ ERROR in register() for username: {}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        log.info("📝 ENTER login(username: {})", request.getUsername());
        log.info(LogHelper.formatSensitiveData("Login Password", request.getPassword()));

        try {
            log.info("🔐 Login attempt for username: {}", request.getUsername());
            log.debug("🔐 Starting authentication process");

            Optional<User> userOpt = userService.authenticateUser(request.getUsername(), request.getPassword());
            log.debug("🔐 Authentication result: {}", userOpt.isPresent() ? "success" : "failed");

            if (userOpt.isEmpty()) {
                log.warn("❌ Authentication failed for username: {}", request.getUsername());
                log.info("📝 EXIT login() - authentication failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid username or password"));
            }

            User user = userOpt.get();
            log.info("✅ User authenticated: {}", user.getId());

            // Create session
            log.debug("🔐 Creating HTTP session");
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", request.getUsername());
            session.setAttribute("role", user.getRole());
            session.setMaxInactiveInterval(1800);
            log.info("🔐 Session created with attributes - userId: {}, username: {}, role: {}",
                    user.getId(), request.getUsername(), user.getRole());

            // Record access time
            log.debug("🗂 Recording access time");
            AccessTime accessTime = new AccessTime();
            accessTime.setId(new ObjectId().toHexString());
            accessTime.setUserId(user.getId());
            accessTime.setTime(LocalDateTime.now());
            accessTimeRepository.save(accessTime);
            log.info("✅ Access time recorded for user: {}", user.getId());

            UserResponse response = userService.toUserResponse(user);
            log.info("✅ Login successful for user: {}", user.getId());

            log.info("📝 EXIT login() - success, user ID: {}", user.getId());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (IllegalStateException e) {
            log.warn("❌ Login failed: {}", e.getMessage());
            log.info("📝 EXIT login() - failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("❌ ERROR in login() for username: {}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request) {
        log.info("📝 ENTER logout()");

        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String userId = (String) session.getAttribute("userId");
                log.info("🔐 Invalidating session for user: {}", userId);
                session.invalidate();
                log.info("✅ Logout successful for user: {}", userId);
            } else {
                log.info("🔐 No active session found for logout");
            }

            log.info("📝 EXIT logout() - completed");
            return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
        } catch (Exception e) {
            log.error("❌ ERROR in logout()", e);
            throw e;
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(HttpServletRequest request) {
        log.info("📝 ENTER getCurrentUser()");

        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                log.warn("🔐 No session found for current user request");
                log.info("📝 EXIT getCurrentUser() - not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Not authenticated"));
            }

            String userId = (String) session.getAttribute("userId");
            log.debug("🔐 Session found, user ID: {}", userId);

            if (userId == null) {
                log.warn("🔐 Session exists but no user ID attribute");
                log.info("📝 EXIT getCurrentUser() - not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Not authenticated"));
            }

            log.debug("🗂 Looking up user by ID: {}", userId);
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("❌ User not found for ID: {}", userId);
                log.info("📝 EXIT getCurrentUser() - user not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found"));
            }

            UserResponse response = userService.toUserResponse(userOpt.get());
            log.info("✅ Current user profile retrieved: {}", userId);

            log.info("📝 EXIT getCurrentUser() - success, user ID: {}", userId);
            return ResponseEntity.ok(ApiResponse.success("User profile retrieved", response));
        } catch (Exception e) {
            log.error("❌ ERROR in getCurrentUser()", e);
            throw e;
        }
    }
}