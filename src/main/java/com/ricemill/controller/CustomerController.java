package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.CustomerDto;
import com.ricemill.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Customer CRUD operations")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    @Operation(summary = "Get all customers", description = "Get paginated list of customers")
    public ApiResponse<Page<CustomerDto.Response>> getAllCustomers(Pageable pageable) {
        return ApiResponse.success(customerService.getAllCustomers(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Get customer details")
    public ApiResponse<CustomerDto.Response> getCustomerById(@PathVariable UUID id) {
        return ApiResponse.success(customerService.getCustomerById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create customer", description = "Create new customer")
    public ApiResponse<CustomerDto.Response> createCustomer(@Valid @RequestBody CustomerDto.CreateRequest request) {
        return ApiResponse.success(customerService.createCustomer(request));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update customer details")
    public ApiResponse<CustomerDto.Response> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerDto.UpdateRequest request) {
        return ApiResponse.success(customerService.updateCustomer(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Soft delete customer")
    public ApiResponse<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success(null);
    }
}
