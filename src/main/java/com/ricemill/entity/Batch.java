package com.ricemill.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "batches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"type", "batch_code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch extends BaseEntity {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;
    
    @Column(nullable = false, length = 50)
    private String batchCode;
    
    @Column(nullable = false)
    private LocalDate batchDate;
    
    @Column(length = 100)
    private String variety;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal moisture;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
}
