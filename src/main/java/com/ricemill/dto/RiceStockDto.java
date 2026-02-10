package com.ricemill.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class RiceStockDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddRequest {

        @NotBlank(message = "Rice type is required")
        @Size(max = 100, message = "Rice type must not exceed 100 characters")
        private String riceType;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Quantity must have maximum 8 digits and 2 decimal places")
        private BigDecimal quantity;

        @Size(max = 10, message = "Unit must not exceed 10 characters")
        private String unit;

        @Size(max = 255, message = "Warehouse location must not exceed 255 characters")
        private String warehouse;

        @Size(max = 20, message = "Grade must not exceed 20 characters")
        private String grade;

        @NotNull(message = "Price per kg is required")
        @DecimalMin(value = "0.01", message = "Price per kg must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Price per kg must have maximum 8 digits and 2 decimal places")
        private BigDecimal pricePerKg;

        @Size(max = 255, message = "Customer name must not exceed 255 characters")
        private String customerName;

        @Size(max = 100, message = "Customer ID must not exceed 100 characters")
        private String customerId;

        @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10-15 digits", flags = Pattern.Flag.CASE_INSENSITIVE)
        private String mobileNumber;

        @Size(max = 50, message = "Status must not exceed 50 characters")
        private String status;

        // Added so builder has variety(String) used by tests
        private String variety;

        // Added so builder has processingDate(LocalDate) used by tests
        private LocalDate processingDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private String riceType;
        private BigDecimal quantity;
        private String unit;
        private String warehouseLocation;
        private String qualityGrade;
        private BigDecimal pricePerKg;
        private String customerName;
        private String customerId;
        private String mobileNumber;
        private String status;
        private String batchNumber;
        private BigDecimal totalValue;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;

        private String createdBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryResponse {
        private Long totalStocks;
        private BigDecimal totalQuantity;
        private BigDecimal totalValue;
        private String unit;
    }
}
