package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.PaddyStockDto;
import com.ricemill.service.PaddyStockService;
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
@RequestMapping("/api/v1/paddy-stock")
@RequiredArgsConstructor
@Tag(name = "Paddy Stock Management", description = "APIs for managing paddy stock inventory")
public class PaddyStockController {

    private final PaddyStockService paddyStockService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Add paddy stock",
        description = "Add new paddy stock to the inventory. Batch number is auto-generated if not provided."
    )
    public ApiResponse<PaddyStockDto.Response> addPaddyStock(@Valid @RequestBody PaddyStockDto.AddRequest request) {
        PaddyStockDto.Response response = paddyStockService.addPaddyStock(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get paddy stock by ID", description = "Retrieve paddy stock details by ID")
    public ApiResponse<PaddyStockDto.Response> getPaddyStockById(@PathVariable UUID id) {
        PaddyStockDto.Response response = paddyStockService.getPaddyStockById(id);
        return ApiResponse.success(response);
    }

    @GetMapping
    @Operation(summary = "Get all paddy stocks", description = "Retrieve all paddy stocks with optional filters")
    public ApiResponse<Page<PaddyStockDto.Response>> getAllPaddyStocks(
            @RequestParam(required = false) String paddyType,
            @RequestParam(required = false) String warehouse,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PaddyStockDto.Response> response = paddyStockService.getAllPaddyStocks(paddyType, warehouse, pageable);
        return ApiResponse.success(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get paddy stock summary", description = "Get summary statistics of paddy stock")
    public ApiResponse<PaddyStockDto.SummaryResponse> getSummary() {
        PaddyStockDto.SummaryResponse response = paddyStockService.getSummary();
        return ApiResponse.success(response);
    }
}

