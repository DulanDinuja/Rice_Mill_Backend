package com.ricemill.service;

import com.ricemill.dto.ReportDto;
import com.ricemill.entity.MovementType;
import com.ricemill.entity.ProductType;
import com.ricemill.entity.ProcessingRecord;
import com.ricemill.entity.StockMovement;
import com.ricemill.repository.ProcessingRecordRepository;
import com.ricemill.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final StockMovementRepository stockMovementRepository;
    private final ProcessingRecordRepository processingRecordRepository;
    
    public ReportDto.MovementReportResponse getMovementReport(ProductType type, MovementType movementType,
                                                               UUID warehouseId, LocalDateTime from, LocalDateTime to,
                                                               Pageable pageable) {
        Page<StockMovement> movements = stockMovementRepository.findByFilters(type, movementType, warehouseId, from, to, pageable);
        
        List<ReportDto.MovementEntry> entries = movements.stream()
                .map(this::toMovementEntry)
                .collect(Collectors.toList());
        
        BigDecimal totalQuantity = movements.stream()
                .map(StockMovement::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return ReportDto.MovementReportResponse.builder()
                .movements(entries)
                .totalQuantity(totalQuantity)
                .count((int) movements.getTotalElements())
                .build();
    }
    
    public ReportDto.ProcessingReportResponse getProcessingReport(LocalDateTime from, LocalDateTime to) {
        List<ProcessingRecord> records = processingRecordRepository.findByDateRange(from, to);
        
        List<ReportDto.ProcessingEntry> entries = records.stream()
                .map(this::toProcessingEntry)
                .collect(Collectors.toList());
        
        BigDecimal totalInput = records.stream()
                .map(ProcessingRecord::getInputQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOutput = records.stream()
                .map(ProcessingRecord::getOutputQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalWaste = records.stream()
                .map(ProcessingRecord::getWasteQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageYield = records.isEmpty() ? BigDecimal.ZERO :
                records.stream()
                        .map(ProcessingRecord::getYieldPercent)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
        
        return ReportDto.ProcessingReportResponse.builder()
                .records(entries)
                .totalInput(totalInput)
                .totalOutput(totalOutput)
                .totalWaste(totalWaste)
                .averageYield(averageYield)
                .build();
    }
    
    private ReportDto.MovementEntry toMovementEntry(StockMovement movement) {
        return ReportDto.MovementEntry.builder()
                .performedAt(movement.getPerformedAt())
                .movementType(movement.getMovementType().name())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .warehouseFrom(movement.getWarehouseFrom() != null ? movement.getWarehouseFrom().getName() : null)
                .warehouseTo(movement.getWarehouseTo() != null ? movement.getWarehouseTo().getName() : null)
                .batchCode(movement.getBatch().getBatchCode())
                .referenceNo(movement.getReferenceNo())
                .performedBy(movement.getPerformedBy().getFullName())
                .build();
    }
    
    private ReportDto.ProcessingEntry toProcessingEntry(ProcessingRecord record) {
        return ReportDto.ProcessingEntry.builder()
                .performedAt(record.getPerformedAt())
                .inputBatchCode(record.getInputBatch().getBatchCode())
                .outputBatchCode(record.getOutputBatch().getBatchCode())
                .warehouseName(record.getWarehouse().getName())
                .inputQty(record.getInputQty())
                .outputQty(record.getOutputQty())
                .wasteQty(record.getWasteQty())
                .yieldPercent(record.getYieldPercent())
                .referenceNo(record.getReferenceNo())
                .performedBy(record.getPerformedBy().getFullName())
                .build();
    }
}
