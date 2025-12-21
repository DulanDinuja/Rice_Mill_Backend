package com.ricemill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private StockSummary paddyStock;
        private StockSummary riceStock;
        private List<WarehouseUtilization> warehouseUtilization;
        private List<LowStockAlert> lowStockAlerts;
        private List<RecentMovement> recentMovements;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockSummary {
        private BigDecimal totalQuantity;
        private String unit;
        private Integer warehouseCount;
        private Integer batchCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseUtilization {
        private String warehouseName;
        private BigDecimal capacity;
        private BigDecimal currentStock;
        private BigDecimal utilizationPercent;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockAlert {
        private String warehouseName;
        private String batchCode;
        private String type;
        private BigDecimal quantity;
        private BigDecimal threshold;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentMovement {
        private String movementType;
        private String type;
        private BigDecimal quantity;
        private String warehouseName;
        private String performedAt;
    }
}
