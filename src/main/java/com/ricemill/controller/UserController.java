package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.UserDto;
import com.ricemill.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management", description = "User CRUD operations (Admin only)")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Get paginated list of users")
    public ApiResponse<Page<UserDto.Response>> getAllUsers(Pageable pageable) {
        return ApiResponse.success(userService.getAllUsers(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get user details")
    public ApiResponse<UserDto.Response> getUserById(@PathVariable UUID id) {
        return ApiResponse.success(userService.getUserById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create user", description = "Create new user")
    public ApiResponse<UserDto.Response> createUser(@Valid @RequestBody UserDto.CreateRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user details")
    public ApiResponse<UserDto.Response> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserDto.UpdateRequest request) {
        return ApiResponse.success(userService.updateUser(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Soft delete user")
    public ApiResponse<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Reset user password", description = "Reset password for a user")
    public ApiResponse<Void> resetPassword(
            @PathVariable UUID id,
            @Valid @RequestBody UserDto.ResetPasswordRequest request) {
        userService.resetPassword(id, request);
        return ApiResponse.success(null);
    }
}
