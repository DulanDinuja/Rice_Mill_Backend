package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.SupplierDto;
import com.ricemill.service.SupplierService;
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
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "Supplier CRUD operations")
public class SupplierController {
    
    private final SupplierService supplierService;
    
    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Get paginated list of suppliers")
    public ApiResponse<Page<SupplierDto.Response>> getAllSuppliers(Pageable pageable) {
        return ApiResponse.success(supplierService.getAllSuppliers(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Get supplier details")
    public ApiResponse<SupplierDto.Response> getSupplierById(@PathVariable UUID id) {
        return ApiResponse.success(supplierService.getSupplierById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create supplier", description = "Create new supplier")
    public ApiResponse<SupplierDto.Response> createSupplier(@Valid @RequestBody SupplierDto.CreateRequest request) {
        return ApiResponse.success(supplierService.createSupplier(request));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update supplier", description = "Update supplier details")
    public ApiResponse<SupplierDto.Response> updateSupplier(
            @PathVariable UUID id,
            @Valid @RequestBody SupplierDto.UpdateRequest request) {
        return ApiResponse.success(supplierService.updateSupplier(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier", description = "Soft delete supplier")
    public ApiResponse<Void> deleteSupplier(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
        return ApiResponse.success(null);
    }
}
