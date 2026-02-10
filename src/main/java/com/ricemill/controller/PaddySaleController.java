package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.PaddySaleDto;
import com.ricemill.entity.PaymentStatus;
import com.ricemill.service.PaddySaleService;
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
@RequestMapping("/api/v1/sales/paddy")
@RequiredArgsConstructor
@Tag(name = "Paddy Sales", description = "APIs for paddy sales management")
public class PaddySaleController {

    private final PaddySaleService paddySaleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create paddy sale",
        description = "Create a new paddy sale transaction. Stock will be automatically reduced."
    )
    public ApiResponse<PaddySaleDto.Response> createSale(@Valid @RequestBody PaddySaleDto.CreateRequest request) {
        PaddySaleDto.Response response = paddySaleService.createSale(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get paddy sale by ID", description = "Retrieve paddy sale details by ID")
    public ApiResponse<PaddySaleDto.Response> getSaleById(@PathVariable UUID id) {
        PaddySaleDto.Response response = paddySaleService.getSaleById(id);
        return ApiResponse.success(response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    @Operation(summary = "Get sales history", description = "Retrieve paddy sales history with filters")
    public ApiResponse<Page<PaddySaleDto.Response>> getSalesHistory(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @PageableDefault(size = 20, sort = "saleDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PaddySaleDto.Response> response = paddySaleService.getSalesHistory(
                customerId, paymentStatus, startDate, endDate, pageable);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    @Operation(summary = "Update payment", description = "Update payment status and amount for a sale")
    public ApiResponse<PaddySaleDto.Response> updatePayment(
            @PathVariable UUID id,
            @Valid @RequestBody PaddySaleDto.UpdatePaymentRequest request) {
        PaddySaleDto.Response response = paddySaleService.updatePayment(id, request);
        return ApiResponse.success(response);
    }
}

