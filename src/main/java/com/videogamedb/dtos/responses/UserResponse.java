package com.videogamedb.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private String id;
    private String username;
    private String email;
    private String emailMasked;
    private String firstName;
    private String lastName;
    private LocalDateTime creationDate;
    private String role;
    private List<String> platforms;
    private LocalDateTime lastLoginDate;
    private int failedLoginAttempts;
    private LocalDateTime accountLockedUntil;
}
