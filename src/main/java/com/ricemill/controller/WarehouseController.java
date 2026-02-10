package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.WarehouseDto;
import com.ricemill.service.WarehouseService;
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
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouse Management", description = "Warehouse CRUD operations")
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    
    @GetMapping
    @Operation(summary = "Get all warehouses", description = "Get paginated list of warehouses")
    public ApiResponse<Page<WarehouseDto.Response>> getAllWarehouses(Pageable pageable) {
        return ApiResponse.success(warehouseService.getAllWarehouses(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID", description = "Get warehouse details")
    public ApiResponse<WarehouseDto.Response> getWarehouseById(@PathVariable UUID id) {
        return ApiResponse.success(warehouseService.getWarehouseById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create warehouse", description = "Create new warehouse")
    public ApiResponse<WarehouseDto.Response> createWarehouse(@Valid @RequestBody WarehouseDto.CreateRequest request) {
        return ApiResponse.success(warehouseService.createWarehouse(request));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update warehouse", description = "Update warehouse details")
    public ApiResponse<WarehouseDto.Response> updateWarehouse(
            @PathVariable UUID id,
            @Valid @RequestBody WarehouseDto.UpdateRequest request) {
        return ApiResponse.success(warehouseService.updateWarehouse(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete warehouse", description = "Soft delete warehouse")
    public ApiResponse<Void> deleteWarehouse(@PathVariable UUID id) {
        warehouseService.deleteWarehouse(id);
        return ApiResponse.success(null);
    }
}
