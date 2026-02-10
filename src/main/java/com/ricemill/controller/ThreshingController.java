package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.ThreshingDto;
import com.ricemill.entity.ThreshingStatus;
import com.ricemill.service.ThreshingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/threshing")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ThreshingController {

    private final ThreshingService threshingService;

    /**
     * Create new threshing record
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ThreshingDto.Response>> createThreshing(
            @Valid @RequestBody ThreshingDto.CreateRequest request) {
        log.info("POST /api/v1/threshing - Creating threshing record");
        ThreshingDto.Response response = threshingService.createThreshingRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * Get all threshing records with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ThreshingDto.Response>>> getAllThreshingRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "threshingDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        log.info("GET /api/v1/threshing - Fetching all threshing records (page: {}, size: {})", page, size);
        Page<ThreshingDto.Response> records = threshingService.getAllThreshingRecords(page, size, sortBy, sortOrder);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Get threshing record by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ThreshingDto.Response>> getThreshingById(
            @PathVariable UUID id) {
        log.info("GET /api/v1/threshing/{} - Fetching threshing record", id);
        ThreshingDto.Response response = threshingService.getThreshingRecordById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get threshing record by batch number
     */
    @GetMapping("/batch/{batchNumber}")
    public ResponseEntity<ApiResponse<ThreshingDto.Response>> getThreshingByBatchNumber(
            @PathVariable String batchNumber) {
        log.info("GET /api/v1/threshing/batch/{} - Fetching threshing record", batchNumber);
        ThreshingDto.Response response = threshingService.getThreshingRecordByBatchNumber(batchNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update threshing record
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ThreshingDto.Response>> updateThreshing(
            @PathVariable UUID id,
            @Valid @RequestBody ThreshingDto.UpdateRequest request) {
        log.info("PUT /api/v1/threshing/{} - Updating threshing record", id);
        ThreshingDto.Response response = threshingService.updateThreshingRecord(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Delete threshing record
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteThreshing(
            @PathVariable UUID id) {
        log.info("DELETE /api/v1/threshing/{} - Deleting threshing record", id);
        threshingService.deleteThreshingRecord(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Update threshing status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ThreshingDto.Response>> updateStatus(
            @PathVariable UUID id,
            @RequestParam ThreshingStatus status) {
        log.info("PATCH /api/v1/threshing/{}/status - Updating status to {}", id, status);
        ThreshingDto.Response response = threshingService.updateThreshingStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get records by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ThreshingDto.Response>>> getByStatus(
            @PathVariable ThreshingStatus status) {
        log.info("GET /api/v1/threshing/status/{} - Fetching records by status", status);
        List<ThreshingDto.Response> records = threshingService.getRecordsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Get records by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<ThreshingDto.Response>>> getByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("GET /api/v1/threshing/date-range - Fetching records from {} to {}", startDate, endDate);
        List<ThreshingDto.Response> records = threshingService.getRecordsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Get records by machine ID
     */
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<ApiResponse<List<ThreshingDto.Response>>> getByMachine(
            @PathVariable String machineId) {
        log.info("GET /api/v1/threshing/machine/{} - Fetching records by machine", machineId);
        List<ThreshingDto.Response> records = threshingService.getRecordsByMachineId(machineId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Get records by paddy variety
     */
    @GetMapping("/variety/{variety}")
    public ResponseEntity<ApiResponse<List<ThreshingDto.Response>>> getByVariety(
            @PathVariable String variety) {
        log.info("GET /api/v1/threshing/variety/{} - Fetching records by variety", variety);
        List<ThreshingDto.Response> records = threshingService.getRecordsByPaddyVariety(variety);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Get high efficiency records
     */
    @GetMapping("/efficiency")
    public ResponseEntity<ApiResponse<List<ThreshingDto.Response>>> getHighEfficiencyRecords(
            @RequestParam(defaultValue = "70.0") Double minEfficiency) {
        log.info("GET /api/v1/threshing/efficiency - Fetching high efficiency records (min: {}%)", minEfficiency);
        List<ThreshingDto.Response> records = threshingService.getHighEfficiencyRecords(minEfficiency);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Get total threshed quantity by date range
     */
    @GetMapping("/total-quantity")
    public ResponseEntity<ApiResponse<Double>> getTotalThreshedQuantity(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("GET /api/v1/threshing/total-quantity - Fetching total quantity from {} to {}", startDate, endDate);
        Double totalQuantity = threshingService.getTotalThreshedQuantity(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(totalQuantity));
    }

    /**
     * Get threshing summary by date range
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ThreshingDto.Summary>> getThreshingSummary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("GET /api/v1/threshing/summary - Fetching summary from {} to {}", startDate, endDate);
        ThreshingDto.Summary summary = threshingService.getThreshingSummary(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}

