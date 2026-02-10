package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.RiceStockDto;
import com.ricemill.service.RiceStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rice-stock")
@RequiredArgsConstructor
@Tag(name = "Rice Stock Management", description = "APIs for managing rice stock inventory")
public class RiceStockController {

    private final RiceStockService riceStockService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Add rice stock",
        description = "Add new rice stock to the inventory. Batch number is auto-generated if not provided."
    )
    public ApiResponse<RiceStockDto.Response> addRiceStock(@Valid @RequestBody RiceStockDto.AddRequest request) {
        RiceStockDto.Response response = riceStockService.addRiceStock(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rice stock by ID", description = "Retrieve rice stock details by ID")
    public ApiResponse<RiceStockDto.Response> getRiceStockById(@PathVariable UUID id) {
        RiceStockDto.Response response = riceStockService.getRiceStockById(id);
        return ApiResponse.success(response);
    }

    @GetMapping("/batch/{batchNumber}")
    @Operation(summary = "Get rice stock by batch number", description = "Retrieve rice stock details by batch number")
    public ApiResponse<RiceStockDto.Response> getRiceStockByBatchNumber(@PathVariable String batchNumber) {
        RiceStockDto.Response response = riceStockService.getRiceStockByBatchNumber(batchNumber);
        return ApiResponse.success(response);
    }

    @GetMapping
    @Operation(summary = "Get all rice stocks", description = "Retrieve all rice stocks with optional filters")
    public ApiResponse<Page<RiceStockDto.Response>> getAllRiceStocks(
            @RequestParam(required = false) String riceType,
            @RequestParam(required = false) String warehouseLocation,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RiceStockDto.Response> response = riceStockService.getAllRiceStocks(riceType, warehouseLocation, pageable);
        return ApiResponse.success(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get rice stock summary", description = "Get summary statistics of rice stock")
    public ApiResponse<RiceStockDto.SummaryResponse> getSummary() {
        RiceStockDto.SummaryResponse response = riceStockService.getSummary();
        return ApiResponse.success(response);
    }
}

