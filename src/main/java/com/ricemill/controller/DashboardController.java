package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.DashboardDto;
import com.ricemill.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
@Tag(name = "Dashboard", description = "Dashboard statistics and overview")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping
    @Operation(summary = "Get dashboard data", description = "Get dashboard statistics and overview")
    public ApiResponse<DashboardDto.Response> getDashboard() {
        return ApiResponse.success(dashboardService.getDashboard());
    }
}
