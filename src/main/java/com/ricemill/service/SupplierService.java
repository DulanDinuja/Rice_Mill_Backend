package com.ricemill.service;

import com.ricemill.dto.SupplierDto;
import com.ricemill.entity.Supplier;
import com.ricemill.exception.ResourceNotFoundException;
import com.ricemill.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    
    public Page<SupplierDto.Response> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }
    
    public SupplierDto.Response getSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        return toResponse(supplier);
    }
    
    @Transactional
    public SupplierDto.Response createSupplier(SupplierDto.CreateRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .active(true)
                .build();
        
        supplier = supplierRepository.save(supplier);
        return toResponse(supplier);
    }
    
    @Transactional
    public SupplierDto.Response updateSupplier(UUID id, SupplierDto.UpdateRequest request) {
        Supplier supplier = supplierRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        
        if (request.getName() != null) {
            supplier.setName(request.getName());
        }
        if (request.getPhone() != null) {
            supplier.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            supplier.setAddress(request.getAddress());
        }
        if (request.getActive() != null) {
            supplier.setActive(request.getActive());
        }
        
        supplier = supplierRepository.save(supplier);
        return toResponse(supplier);
    }
    
    @Transactional
    public void deleteSupplier(UUID id) {
        Supplier supplier = supplierRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        supplier.setDeletedAt(LocalDateTime.now());
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }
    
    private SupplierDto.Response toResponse(Supplier supplier) {
        return SupplierDto.Response.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .active(supplier.getActive())
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}
