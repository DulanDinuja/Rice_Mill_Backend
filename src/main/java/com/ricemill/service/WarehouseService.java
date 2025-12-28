package com.ricemill.service;

import com.ricemill.dto.WarehouseDto;
import com.ricemill.entity.Warehouse;
import com.ricemill.exception.BusinessException;
import com.ricemill.exception.ResourceNotFoundException;
import com.ricemill.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    
    private final WarehouseRepository warehouseRepository;
    
    public Page<WarehouseDto.Response> getAllWarehouses(Pageable pageable) {
        return warehouseRepository.findByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }
    
    public WarehouseDto.Response getWarehouseById(UUID id) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        return toResponse(warehouse);
    }
    
    @Transactional
    public WarehouseDto.Response createWarehouse(WarehouseDto.CreateRequest request) {
        if (warehouseRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BusinessException("Warehouse name already exists");
        }
        
        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .location(request.getLocation())
                .capacity(request.getCapacity() != null ? request.getCapacity() : BigDecimal.ZERO)
                .notes(request.getNotes())
//                .active(true)
                .build();
        
        warehouse = warehouseRepository.save(warehouse);
        return toResponse(warehouse);
    }
    
    @Transactional
    public WarehouseDto.Response updateWarehouse(UUID id, WarehouseDto.UpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        
        if (request.getName() != null) {
            warehouse.setName(request.getName());
        }
        
        if (request.getLocation() != null) {
            warehouse.setLocation(request.getLocation());
        }
        
        if (request.getCapacity() != null) {
            warehouse.setCapacity(request.getCapacity());
        }
        
        if (request.getNotes() != null) {
            warehouse.setNotes(request.getNotes());
        }
        
        if (request.getActive() != null) {
            warehouse.setActive(request.getActive());
        }
        
        warehouse = warehouseRepository.save(warehouse);
        return toResponse(warehouse);
    }
    
    @Transactional
    public void deleteWarehouse(UUID id) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        warehouse.setDeletedAt(LocalDateTime.now());
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
    }
    
    private WarehouseDto.Response toResponse(Warehouse warehouse) {
        return WarehouseDto.Response.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .capacity(warehouse.getCapacity())
                .notes(warehouse.getNotes())
                .active(warehouse.getActive())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }
}
