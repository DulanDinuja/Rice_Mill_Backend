package com.ricemill.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processing_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessingRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_batch_id", nullable = false)
    private Batch inputBatch;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "output_batch_id", nullable = false)
    private Batch outputBatch;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal inputQty;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal outputQty;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal wasteQty = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal yieldPercent;
    
    @Column(length = 100)
    private String referenceNo;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;
    
    @Column(nullable = false)
    private LocalDateTime performedAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
