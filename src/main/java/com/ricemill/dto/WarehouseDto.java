package com.ricemill.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WarehouseDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private String name;
        private String location;
        private BigDecimal capacity;
        private String notes;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Warehouse name is required")
        private String name;
        
        private String location;
        
        @DecimalMin(value = "0.0", message = "Capacity must be non-negative")
        private BigDecimal capacity;
        
        private String notes;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String location;
        
        @DecimalMin(value = "0.0", message = "Capacity must be non-negative")
        private BigDecimal capacity;
        
        private String notes;
        private Boolean active;
    }
}
