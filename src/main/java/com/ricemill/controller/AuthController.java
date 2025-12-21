package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.AuthDto;
import com.ricemill.entity.User;
import com.ricemill.exception.ResourceNotFoundException;
import com.ricemill.repository.UserRepository;
import com.ricemill.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {
    
    private final AuthService authService;
    private final UserRepository userRepository;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return access and refresh tokens")
    public ApiResponse<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ApiResponse<AuthDto.RefreshResponse> refresh(@Valid @RequestBody AuthDto.RefreshRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke refresh token")
    public ApiResponse<Void> logout(@Valid @RequestBody AuthDto.RefreshRequest request) {
        authService.logout(request);
        return ApiResponse.success(null);
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get authenticated user information")
    public ApiResponse<AuthDto.UserInfo> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        AuthDto.UserInfo userInfo = AuthDto.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
        
        return ApiResponse.success(userInfo);
    }
    
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register new user", description = "Create new user account (Admin only)")
    public ApiResponse<AuthDto.UserInfo> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }
}
