package com.ricemill.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "paddy_stock", indexes = {
    @Index(name = "idx_paddy_stock_paddy_type", columnList = "paddy_type"),
    @Index(name = "idx_paddy_stock_warehouse", columnList = "warehouse"),
    @Index(name = "idx_paddy_stock_status", columnList = "status"),
    @Index(name = "idx_paddy_stock_customer_id", columnList = "customer_id"),
    @Index(name = "idx_paddy_stock_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaddyStock extends BaseEntity {

    @Column(name = "paddy_type", nullable = false, length = 100)
    private String paddyType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(length = 20)
    private String unit;

    @Column(length = 255)
    private String warehouse;

    @Column(name = "moisture_level", precision = 5, scale = 2)
    private BigDecimal moistureLevel;

    @Column(length = 255)
    private String supplier;

    @Column(name = "price_per_kg", precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(length = 50)
    private String status;

    @Column(name = "total_value", precision = 12, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @PrePersist
    @PreUpdate
    private void calculateTotalValue() {
        if (quantity != null && pricePerKg != null) {
            this.totalValue = quantity.multiply(pricePerKg);
        }
    }
}
