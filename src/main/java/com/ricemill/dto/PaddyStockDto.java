package com.ricemill.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaddyStockDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddRequest {

        @NotBlank(message = "Paddy type is required")
        @Size(max = 100, message = "Paddy type must not exceed 100 characters")
        private String paddyType;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Quantity must have maximum 8 digits and 2 decimal places")
        private BigDecimal quantity;

        @Size(max = 20, message = "Unit must not exceed 20 characters")
        private String unit;

        @Size(max = 255, message = "Warehouse must not exceed 255 characters")
        private String warehouse;

        @DecimalMin(value = "0", message = "Moisture level must be between 0 and 100")
        @DecimalMax(value = "100", message = "Moisture level must be between 0 and 100")
        @Digits(integer = 3, fraction = 2, message = "Moisture level must have maximum 3 digits and 2 decimal places")
        private BigDecimal moistureLevel;

        @Size(max = 255, message = "Supplier name must not exceed 255 characters")
        private String supplier;

        @DecimalMin(value = "0.01", message = "Price per kg must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Price per kg must have maximum 8 digits and 2 decimal places")
        private BigDecimal pricePerKg;

        @Size(max = 255, message = "Customer name must not exceed 255 characters")
        private String customerName;

        private String customerId;

        @Pattern(regexp = "^[0-9]{10,20}$", message = "Mobile number must be a valid phone number (10-20 digits)")
        private String mobileNumber;

        @Size(max = 50, message = "Status must not exceed 50 characters")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private String paddyType;
        private BigDecimal quantity;
        private String unit;
        private String warehouse;
        private BigDecimal moistureLevel;
        private String supplier;
        private BigDecimal pricePerKg;
        private String customerName;
        private String customerId;
        private String mobileNumber;
        private String status;
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

