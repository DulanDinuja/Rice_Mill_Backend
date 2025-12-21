package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.SettingsDto;
import com.ricemill.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Application settings management")
public class SettingsController {
    
    private final SettingsService settingsService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get settings", description = "Get application settings")
    public ApiResponse<SettingsDto.Response> getSettings() {
        return ApiResponse.success(settingsService.getSettings());
    }
    
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update settings", description = "Update application settings (Admin only)")
    public ApiResponse<SettingsDto.Response> updateSettings(@Valid @RequestBody SettingsDto.UpdateRequest request) {
        return ApiResponse.success(settingsService.updateSettings(request));
    }
}
