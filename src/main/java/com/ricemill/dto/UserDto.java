package com.ricemill.dto;

import com.ricemill.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class UserDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private String username;
        private String email;
        private String fullName;
        private Boolean active;
        private Set<UserRole> roles;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50)
        private String username;
        
        @NotBlank(message = "Email is required")
        @Email
        private String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6)
        private String password;
        
        @NotBlank(message = "Full name is required")
        private String fullName;
        
        private Set<UserRole> roles;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String email;
        private String fullName;
        private Boolean active;
        private Set<UserRole> roles;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordRequest {
        @NotBlank(message = "New password is required")
        @Size(min = 6)
        private String newPassword;
    }
}
