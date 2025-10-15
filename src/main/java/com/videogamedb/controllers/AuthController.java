package com.videogamedb.controllers;

import com.videogamedb.dtos.requests.LoginRequest;
import com.videogamedb.dtos.requests.RegisterRequest;
import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.dtos.responses.UserResponse;
import com.videogamedb.models.AccessTime;
import com.videogamedb.models.User;
import com.videogamedb.repositories.AccessTimeRepository;
import com.videogamedb.services.UserService;
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

/**
 * Authentication and user profile endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AccessTimeRepository accessTimeRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("📝 Registration request for username: {}", request.getUsername());

        try {
            User user = userService.registerUser(request);
            UserResponse response = userService.toUserResponse(user);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
        } catch (IllegalArgumentException e) {
            log.warn("❌ Registration failed: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        log.info("🔐 Login attempt for username: {}", request.getUsername());

        try {
            Optional<User> userOpt = userService.authenticateUser(request.getUsername(), request.getPassword());

            if (userOpt.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username or password"));
            }

            User user = userOpt.get();

            // Create session
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", request.getUsername());
            session.setAttribute("role", user.getRole());
            session.setMaxInactiveInterval(1800); // 30 minutes

            // Record access time
            AccessTime accessTime = new AccessTime();
            accessTime.setId(new ObjectId().toHexString());
            accessTime.setUserId(user.getId());
            accessTime.setTime(LocalDateTime.now());
            accessTimeRepository.save(accessTime);

            UserResponse response = userService.toUserResponse(user);
            log.info("✅ Login successful for user: {}", user.getId());

            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (IllegalStateException e) {
            log.warn("❌ Login failed: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userId = (String) session.getAttribute("userId");
            session.invalidate();
            log.info("✅ Logout successful for user: {}", userId);
        }

        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Not authenticated"));
        }

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Not authenticated"));
        }

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found"));
        }

        UserResponse response = userService.toUserResponse(userOpt.get());
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", response));
    }
}
