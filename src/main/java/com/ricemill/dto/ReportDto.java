package com.ricemill.dto;

import com.ricemill.entity.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReportDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockLedgerResponse {
        private List<LedgerEntry> entries;
        private BigDecimal totalInbound;
        private BigDecimal totalOutbound;
        private BigDecimal netBalance;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LedgerEntry {
        private LocalDateTime date;
        private String movementType;
        private String batchCode;
        private String warehouseName;
        private BigDecimal inbound;
        private BigDecimal outbound;
        private BigDecimal balance;
        private String referenceNo;
        private String performedBy;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementReportResponse {
        private List<MovementEntry> movements;
        private BigDecimal totalQuantity;
        private Integer count;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementEntry {
        private LocalDateTime performedAt;
        private String movementType;
        private ProductType type;
        private BigDecimal quantity;
        private String warehouseFrom;
        private String warehouseTo;
        private String batchCode;
        private String referenceNo;
        private String performedBy;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessingReportResponse {
        private List<ProcessingEntry> records;
        private BigDecimal totalInput;
        private BigDecimal totalOutput;
        private BigDecimal totalWaste;
        private BigDecimal averageYield;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessingEntry {
        private LocalDateTime performedAt;
        private String inputBatchCode;
        private String outputBatchCode;
        private String warehouseName;
        private BigDecimal inputQty;
        private BigDecimal outputQty;
        private BigDecimal wasteQty;
        private BigDecimal yieldPercent;
        private String referenceNo;
        private String performedBy;
    }
}
