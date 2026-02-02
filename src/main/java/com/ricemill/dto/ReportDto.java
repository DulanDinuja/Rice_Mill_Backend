package com.ricemill.dto;

import com.ricemill.entity.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    // Enhanced Report DTOs for Ameera Rice Inventory System

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportDataResponse {
        private List<ReportDataEntry> data;
        private PaginationInfo pagination;
        private ReportSummary summary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportDataEntry {
        private UUID id;
        private String paddyType;
        private String riceType;
        private BigDecimal quantity;
        private String unit;
        private BigDecimal moistureLevel;
        private String warehouse;
        private String supplier;
        private BigDecimal pricePerKg;
        private String actionType;
        private LocalDateTime date;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private Integer currentPage;
        private Integer totalPages;
        private Long totalRecords;
        private Integer limit;
        private Boolean hasNextPage;
        private Boolean hasPreviousPage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportSummary {
        private Long totalRecords;
        private BigDecimal totalQuantity;
        private DateRange dateRange;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        private LocalDateTime from;
        private LocalDateTime to;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataResponse {
        private List<ChartDataEntry> data;
        private ChartSummary summary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataEntry {
        private String period;
        private String label;
        private BigDecimal rice;
        private BigDecimal paddy;
        private BigDecimal quantity;
        private Long recordCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartSummary {
        private BigDecimal totalQuantity;
        private BigDecimal averagePerPeriod;
        private Integer periods;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportRequest {
        private String reportType;
        private String format;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private ExportFilters filters;
        private ExportOptions options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportFilters {
        private String warehouse;
        private String paddyType;
        private String riceType;
        private String supplier;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportOptions {
        private Boolean includeChart;
        private Boolean includeSummary;
        private String fileName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportTypeInfo {
        private String value;
        private String label;
        private String category;
        private String description;
        private List<String> columns;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseListResponse {
        private UUID id;
        private String name;
        private String location;
        private BigDecimal capacity;
        private BigDecimal currentStock;
        private Boolean active;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierListResponse {
        private UUID id;
        private String name;
        private String contactNumber;
        private String email;
        private String address;
        private Boolean active;
    }
}
