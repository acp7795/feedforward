package com.videogamedb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Field("username_enc")
    @Indexed(unique = true)
    private String usernameEnc;
    
    @Field("username_hash")
    @Indexed(unique = true)
    private String usernameHash;
    
    @Field("email_enc")
    @Indexed(unique = true)
    private String emailEnc;
    
    @Field("email_masked")
    private String emailMasked;
    
    @Field("firstName_enc")
    private String firstNameEnc;
    
    @Field("lastName_enc")
    private String lastNameEnc;
    
    @Field("password_hash")
    private String passwordHash;
    
    @Field("password_salt")
    private String passwordSalt;
    
    @Field("creationDate")
    @Indexed
    private LocalDateTime creationDate;
    
    @Indexed
    private String role; // "USER" or "ADMIN"
    
    private List<String> platforms; // Platform IDs user is interested in
    
    @Field("audit_token")
    private String auditToken;
    
    @Field("failed_login_attempts")
    private int failedLoginAttempts;
    
    @Field("account_locked_until")
    @Indexed
    private LocalDateTime accountLockedUntil;
    
    @Field("max_login_attempts")
    private int maxLoginAttempts = 5;
    
    @Field("account_lock_minutes")
    private int accountLockMinutes = 30;
    
    @Field("last_login_date")
    private LocalDateTime lastLoginDate;
    
    @Field("password_iterations")
    private int passwordIterations = 310000;
}
