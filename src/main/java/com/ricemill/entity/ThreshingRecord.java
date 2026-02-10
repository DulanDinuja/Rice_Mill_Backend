package com.ricemill.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "threshing_records", indexes = {
    @Index(name = "idx_threshing_batch_number", columnList = "batch_number"),
    @Index(name = "idx_threshing_date", columnList = "threshing_date"),
    @Index(name = "idx_threshing_status", columnList = "status"),
    @Index(name = "idx_threshing_machine_id", columnList = "machine_id"),
    @Index(name = "idx_threshing_paddy_variety", columnList = "paddy_variety"),
    @Index(name = "idx_threshing_operator", columnList = "mill_operator_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreshingRecord extends BaseEntity {

    @Column(name = "batch_number", nullable = false, unique = true, length = 50)
    private String batchNumber;

    @Column(name = "paddy_stock_id", nullable = false)
    private UUID paddyStockId;

    @Column(name = "paddy_variety", nullable = false, length = 100)
    private String paddyVariety;

    @Column(name = "input_paddy_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal inputPaddyQuantity;

    @Column(name = "output_rice_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal outputRiceQuantity;

    @Column(name = "wastage_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal wastageQuantity;

    @Column(name = "threshing_efficiency", nullable = false, precision = 5, scale = 2)
    private BigDecimal threshingEfficiency;

    @Column(name = "threshing_date", nullable = false)
    private LocalDate threshingDate;

    @Column(name = "mill_operator_name", nullable = false, length = 100)
    private String millOperatorName;

    @Column(name = "machine_id", nullable = false, length = 50)
    private String machineId;

    @Column(name = "machine_type", length = 100)
    private String machineType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ThreshingStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "rice_stock_id")
    private UUID riceStockId;

    @PrePersist
    @PreUpdate
    private void calculateEfficiencyAndWastage() {
        if (inputPaddyQuantity != null && outputRiceQuantity != null) {
            // Calculate wastage
            this.wastageQuantity = inputPaddyQuantity.subtract(outputRiceQuantity);

            // Calculate efficiency percentage
            if (inputPaddyQuantity.compareTo(BigDecimal.ZERO) > 0) {
                this.threshingEfficiency = outputRiceQuantity
                    .divide(inputPaddyQuantity, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }
        }
    }
}

