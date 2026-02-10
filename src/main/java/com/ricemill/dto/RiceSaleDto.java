package com.ricemill.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ricemill.entity.CustomerType;
import com.ricemill.entity.PaymentMethod;
import com.ricemill.entity.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class RiceSaleDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotNull(message = "Rice stock ID is required")
        private UUID riceStockId;

        @NotBlank(message = "Batch number is required")
        private String batchNumber;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        @Pattern(regexp = "^(1KG|5KG|10KG|25KG|50KG|Bulk)$", message = "Package type must be valid")
        private String packageType;

        @Min(value = 0, message = "Number of packages cannot be negative")
        private Integer numberOfPackages;

        @NotNull(message = "Price per kg is required")
        @DecimalMin(value = "0.01", message = "Price per kg must be greater than 0")
        private BigDecimal pricePerKg;

        @DecimalMin(value = "0.01", message = "Price per package must be greater than 0")
        private BigDecimal pricePerPackage;

        @NotBlank(message = "Customer name is required")
        private String customerName;

        @NotBlank(message = "Customer contact is required")
        @Pattern(regexp = "^[0-9]{10,20}$", message = "Customer contact must be 10-20 digits")
        private String customerContact;

        @Email(message = "Invalid email format")
        private String customerEmail;

        private String customerAddress;

        private CustomerType customerType = CustomerType.RETAIL;

        @NotNull(message = "Sale date is required")
        @PastOrPresent(message = "Sale date cannot be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate saleDate;

        @NotNull(message = "Payment method is required")
        private PaymentMethod paymentMethod;

        @NotNull(message = "Payment status is required")
        private PaymentStatus paymentStatus;

        @NotNull(message = "Paid amount is required")
        @DecimalMin(value = "0", message = "Paid amount cannot be negative")
        private BigDecimal paidAmount;

        @DecimalMin(value = "0", message = "Discount must be between 0 and 100")
        @DecimalMax(value = "100", message = "Discount must be between 0 and 100")
        private BigDecimal discount = BigDecimal.ZERO;

        @DecimalMin(value = "0", message = "Tax rate must be between 0 and 100")
        @DecimalMax(value = "100", message = "Tax rate must be between 0 and 100")
        private BigDecimal taxRate = BigDecimal.ZERO;

        private Boolean deliveryRequired = false;

        private String deliveryAddress;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate deliveryDate;

        @DecimalMin(value = "0", message = "Delivery charge cannot be negative")
        private BigDecimal deliveryCharge = BigDecimal.ZERO;

        private String vehicleNumber;

        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private UUID saleId;
        private String invoiceNumber;
        private String batchNumber;
        private String riceType;
        private String variety;
        private BigDecimal quantity;
        private Integer numberOfPackages;
        private BigDecimal pricePerKg;
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private BigDecimal deliveryCharge;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal balanceAmount;
        private PaymentStatus paymentStatus;
        private String customerName;
        private CustomerType customerType;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime saleDate;

        private String invoiceUrl;
        private BigDecimal remainingStock;
        private String createdBy;
    }
}

