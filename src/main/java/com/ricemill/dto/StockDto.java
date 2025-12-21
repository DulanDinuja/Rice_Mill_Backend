package com.ricemill.dto;

import com.ricemill.entity.ProductType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class StockDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryResponse {
        private UUID id;
        private UUID warehouseId;
        private String warehouseName;
        private UUID batchId;
        private String batchCode;
        private ProductType type;
        private BigDecimal quantity;
        private String unit;
        private String variety;
        private LocalDate batchDate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryResponse {
        private ProductType type;
        private BigDecimal totalQuantity;
        private String unit;
        private Integer warehouseCount;
        private Integer batchCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InboundRequest {
        @NotNull(message = "Product type is required")
        private ProductType type;
        
        @NotNull(message = "Warehouse ID is required")
        private UUID warehouseId;
        
        @NotBlank(message = "Batch code is required")
        private String batchCode;
        
        @NotNull(message = "Batch date is required")
        private LocalDate batchDate;
        
        private String variety;
        
        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be positive")
        private BigDecimal quantity;
        
        private String unit = "KG";
        
        private UUID supplierId;
        
        private BigDecimal moisture;
        
        private String referenceNo;
        
        private String notes;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutboundRequest {
        @NotNull(message = "Product type is required")
        private ProductType type;
        
        @NotNull(message = "Warehouse ID is required")
        private UUID warehouseId;
        
        @NotNull(message = "Batch ID is required")
        private UUID batchId;
        
        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be positive")
        private BigDecimal quantity;
        
        private String unit = "KG";
        
        private UUID customerId;
        
        private String referenceNo;
        
        private String reason;
        
        private Boolean adminOverride = false;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest {
        @NotNull(message = "Product type is required")
        private ProductType type;
        
        @NotNull(message = "From warehouse ID is required")
        private UUID fromWarehouseId;
        
        @NotNull(message = "To warehouse ID is required")
        private UUID toWarehouseId;
        
        @NotNull(message = "Batch ID is required")
        private UUID batchId;
        
        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be positive")
        private BigDecimal quantity;
        
        private String unit = "KG";
        
        private String referenceNo;
        
        private String reason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdjustmentRequest {
        @NotNull(message = "Product type is required")
        private ProductType type;
        
        @NotNull(message = "Warehouse ID is required")
        private UUID warehouseId;
        
        @NotNull(message = "Batch ID is required")
        private UUID batchId;
        
        @NotNull(message = "Quantity is required")
        private BigDecimal quantity;
        
        private String unit = "KG";
        
        @NotBlank(message = "Reason is required for adjustments")
        private String reason;
        
        private String referenceNo;
        
        private Boolean adminOverride = false;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessRequest {
        @NotNull(message = "Warehouse ID is required")
        private UUID warehouseId;
        
        @NotNull(message = "Input batch ID is required")
        private UUID inputBatchId;
        
        @NotBlank(message = "Output batch code is required")
        private String outputBatchCode;
        
        @NotNull(message = "Output batch date is required")
        private LocalDate outputBatchDate;
        
        private String outputVariety;
        
        @NotNull(message = "Input quantity is required")
        @DecimalMin(value = "0.01", message = "Input quantity must be positive")
        private BigDecimal inputQty;
        
        @NotNull(message = "Output quantity is required")
        @DecimalMin(value = "0.01", message = "Output quantity must be positive")
        private BigDecimal outputQty;
        
        @DecimalMin(value = "0.0", message = "Waste quantity must be non-negative")
        private BigDecimal wasteQty = BigDecimal.ZERO;
        
        private String referenceNo;
        
        private String notes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementResponse {
        private UUID id;
        private String movementType;
        private ProductType type;
        private BigDecimal quantity;
        private String unit;
        private String warehouseFromName;
        private String warehouseToName;
        private String batchCode;
        private String referenceNo;
        private String reason;
        private String performedByName;
        private LocalDateTime performedAt;
    }
}
