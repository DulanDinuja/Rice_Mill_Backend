package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.ReportDto;
import com.ricemill.entity.MovementType;
import com.ricemill.entity.ProductType;
import com.ricemill.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Reports", description = "Reporting and analytics endpoints")
public class ReportController {
    
    private final ReportService reportService;


    @GetMapping
    @Operation(summary = "Get reports data", description = "Fetch filtered report data based on type and criteria")
    public ApiResponse<ReportDto.ReportDataResponse> getReports(
            @RequestParam String reportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String paddyType,
            @RequestParam(required = false) String riceType,
            @RequestParam(required = false) String supplier,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "100") Integer limit) {
        return ApiResponse.success(reportService.getReports(reportType, fromDate, toDate, warehouse,
                paddyType, riceType, supplier, page, limit));
    }

    @GetMapping("/chart")
    @Operation(summary = "Get chart data", description = "Get aggregated data for chart visualization")
    public ApiResponse<ReportDto.ChartDataResponse> getChartData(
            @RequestParam String reportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String paddyType,
            @RequestParam(required = false) String riceType,
            @RequestParam(required = false) String supplier,
            @RequestParam(defaultValue = "month") String groupBy) {
        return ApiResponse.success(reportService.getChartData(reportType, fromDate, toDate, warehouse,
                paddyType, riceType, supplier, groupBy));
    }

    @PostMapping("/export")
    @Operation(summary = "Export report", description = "Export report data to CSV or PDF format")
    public ResponseEntity<Resource> exportReport(@Valid @RequestBody ReportDto.ExportRequest request) {
        Resource resource = reportService.exportReport(request);

        String contentType = request.getFormat().equalsIgnoreCase("pdf")
                ? "application/pdf"
                : "text/csv";

        String fileName = (request.getOptions() != null && request.getOptions().getFileName() != null)
                ? request.getOptions().getFileName()
                : reportService.generateFileName(request.getReportType(), request.getFormat());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/warehouses")
    @Operation(summary = "Get warehouses list", description = "Get list of all active warehouses")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<List<ReportDto.WarehouseListResponse>> getWarehouses() {
        return ApiResponse.success(reportService.getAllActiveWarehouses());
    }

    @GetMapping("/suppliers")
    @Operation(summary = "Get suppliers list", description = "Get list of all active suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<List<ReportDto.SupplierListResponse>> getSuppliers() {
        return ApiResponse.success(reportService.getAllActiveSuppliers());
    }

    @GetMapping("/types")
    @Operation(summary = "Get report types", description = "Get available report types with metadata")
    public ApiResponse<List<ReportDto.ReportTypeInfo>> getReportTypes() {
        return ApiResponse.success(reportService.getReportTypes());
    }
}
