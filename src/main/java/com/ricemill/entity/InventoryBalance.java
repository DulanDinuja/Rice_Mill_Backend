package com.ricemill.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory_balance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"warehouse_id", "batch_id", "type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryBalance extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal quantity = BigDecimal.ZERO;
    
    @Column(nullable = false, length = 10)
    private String unit = "KG";
    
    @Version
    private Integer version = 0;
}
