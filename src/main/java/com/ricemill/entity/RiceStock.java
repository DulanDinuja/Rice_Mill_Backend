package com.ricemill.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rice_stock", indexes = {
    @Index(name = "idx_rice_stock_batch_number", columnList = "batch_number"),
    @Index(name = "idx_rice_stock_rice_type", columnList = "rice_type"),
    @Index(name = "idx_rice_stock_warehouse", columnList = "warehouse_location"),
    @Index(name = "idx_rice_stock_active", columnList = "active"),
    @Index(name = "idx_rice_stock_customer_id", columnList = "customer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiceStock extends BaseEntity {

    @Column(name = "rice_type", nullable = false, length = 100)
    private String riceType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(length = 10)
    private String unit;

    @Column(name = "warehouse_location", length = 255)
    private String warehouseLocation;

    @Column(name = "quality_grade", length = 20)
    private String qualityGrade;

    @Column(name = "price_per_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "customer_id", length = 100)
    private String customerId;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(length = 50)
    private String status;

    @Column(name = "batch_number", nullable = false, unique = true, length = 50)
    private String batchNumber;


    @Column(name = "total_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalValue;


    @PrePersist
    @PreUpdate
    private void calculateTotalValue() {
        if (quantity != null && pricePerKg != null) {
            this.totalValue = quantity.multiply(pricePerKg);
        }
    }
}
