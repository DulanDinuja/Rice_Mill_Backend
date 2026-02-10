package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.RiceSaleDto;
import com.ricemill.entity.PaymentStatus;
import com.ricemill.service.RiceSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/rice")
@RequiredArgsConstructor
@Tag(name = "Rice Sales", description = "APIs for rice sales management")
public class RiceSaleController {

    private final RiceSaleService riceSaleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create rice sale",
        description = "Create a new rice sale transaction. Stock will be automatically reduced."
    )
    public ApiResponse<RiceSaleDto.Response> createSale(@Valid @RequestBody RiceSaleDto.CreateRequest request) {
        RiceSaleDto.Response response = riceSaleService.createSale(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rice sale by ID", description = "Retrieve rice sale details by ID")
    public ApiResponse<RiceSaleDto.Response> getSaleById(@PathVariable UUID id) {
        RiceSaleDto.Response response = riceSaleService.getSaleById(id);
        return ApiResponse.success(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get sales history", description = "Retrieve rice sales history with filters")
    public ApiResponse<Page<RiceSaleDto.Response>> getSalesHistory(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @PageableDefault(size = 20, sort = "saleDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RiceSaleDto.Response> response = riceSaleService.getSalesHistory(
                customerId, paymentStatus, startDate, endDate, pageable);
        return ApiResponse.success(response);
    }
}

