package com.ricemill.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    private String location;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal capacity = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
}
