package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.ReportDto;
import com.ricemill.entity.MovementType;
import com.ricemill.entity.ProductType;
import com.ricemill.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Reports", description = "Reporting and analytics endpoints")
public class ReportController {
    
    private final ReportService reportService;
    
    @GetMapping("/movements")
    @Operation(summary = "Movement report", description = "Get stock movement report with filters")
    public ApiResponse<ReportDto.MovementReportResponse> getMovementReport(
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) MovementType movementType,
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        return ApiResponse.success(reportService.getMovementReport(type, movementType, warehouseId, from, to, pageable));
    }
    
    @GetMapping("/processing")
    @Operation(summary = "Processing report", description = "Get processing records report")
    public ApiResponse<ReportDto.ProcessingReportResponse> getProcessingReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ApiResponse.success(reportService.getProcessingReport(from, to));
    }
}
