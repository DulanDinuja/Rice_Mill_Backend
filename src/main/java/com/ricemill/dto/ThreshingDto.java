package com.ricemill.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ricemill.entity.ThreshingStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ThreshingDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotNull(message = "Paddy stock ID is required")
        private UUID paddyStockId;

        @NotBlank(message = "Paddy variety is required")
        private String paddyVariety;

        @NotNull(message = "Input paddy quantity is required")
        @DecimalMin(value = "0.01", message = "Input quantity must be greater than 0")
        private BigDecimal inputPaddyQuantity;

        @NotNull(message = "Output rice quantity is required")
        @DecimalMin(value = "0.01", message = "Output quantity must be greater than 0")
        private BigDecimal outputRiceQuantity;

        @NotNull(message = "Threshing date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate threshingDate;

        @NotBlank(message = "Mill operator name is required")
        private String millOperatorName;

        @NotBlank(message = "Machine ID is required")
        private String machineId;

        private String machineType;

        private String notes;

        @NotNull(message = "Status is required")
        private ThreshingStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String paddyVariety;

        @DecimalMin(value = "0.01", message = "Input quantity must be greater than 0")
        private BigDecimal inputPaddyQuantity;

        @DecimalMin(value = "0.01", message = "Output quantity must be greater than 0")
        private BigDecimal outputRiceQuantity;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate threshingDate;

        private String millOperatorName;

        private String machineId;

        private String machineType;

        private String notes;

        private ThreshingStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private String batchNumber;
        private UUID paddyStockId;
        private String paddyVariety;
        private BigDecimal inputPaddyQuantity;
        private BigDecimal outputRiceQuantity;
        private BigDecimal wastageQuantity;
        private BigDecimal threshingEfficiency;
        private LocalDate threshingDate;
        private String millOperatorName;
        private String machineId;
        private String machineType;
        private ThreshingStatus status;
        private String notes;
        private UUID riceStockId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UUID createdBy;
        private UUID updatedBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EfficiencyReport {
        private UUID id;
        private String batchNumber;
        private String paddyVariety;
        private BigDecimal inputPaddyQuantity;
        private BigDecimal outputRiceQuantity;
        private BigDecimal threshingEfficiency;
        private LocalDate threshingDate;
        private String millOperatorName;
        private String machineId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long totalRecords;
        private BigDecimal totalInputPaddy;
        private BigDecimal totalOutputRice;
        private BigDecimal totalWastage;
        private BigDecimal averageEfficiency;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}

