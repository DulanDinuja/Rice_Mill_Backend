package com.ricemill.service;

import com.ricemill.dto.ThreshingDto;
import com.ricemill.entity.*;
import com.ricemill.exception.*;
import com.ricemill.repository.PaddyStockRepository;
import com.ricemill.repository.RiceStockRepository;
import com.ricemill.repository.ThreshingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreshingService {

    private final ThreshingRepository threshingRepository;
    private final PaddyStockRepository paddyStockRepository;
    private final RiceStockRepository riceStockRepository;

    @Value("${threshing.min.efficiency:60.0}")
    private Double minEfficiency;

    @Value("${threshing.max.efficiency:75.0}")
    private Double maxEfficiency;

    @Value("${threshing.batch.prefix:TH}")
    private String batchPrefix;

    /**
     * Create new threshing record
     */
    @Transactional
    public ThreshingDto.Response createThreshingRecord(ThreshingDto.CreateRequest request) {
        log.info("Creating threshing record for paddy stock ID: {}", request.getPaddyStockId());

        // Validate request data
        validateThreshingRequest(request);

        // Get and validate paddy stock
        PaddyStock paddyStock = paddyStockRepository.findByIdAndDeletedAtIsNull(request.getPaddyStockId())
            .orElseThrow(() -> new StockNotFoundException("Paddy stock not found with ID: " + request.getPaddyStockId()));

        // Check if sufficient stock is available
        if (paddyStock.getQuantity().compareTo(request.getInputPaddyQuantity()) < 0) {
            throw new InsufficientPaddyStockException(
                String.format("Insufficient paddy stock. Available: %.2f kg, Required: %.2f kg",
                    paddyStock.getQuantity(), request.getInputPaddyQuantity())
            );
        }

        // Generate batch number
        String batchNumber = generateBatchNumber();

        // Create threshing record
        ThreshingRecord record = ThreshingRecord.builder()
            .batchNumber(batchNumber)
            .paddyStockId(request.getPaddyStockId())
            .paddyVariety(request.getPaddyVariety())
            .inputPaddyQuantity(request.getInputPaddyQuantity())
            .outputRiceQuantity(request.getOutputRiceQuantity())
            .threshingDate(request.getThreshingDate())
            .millOperatorName(request.getMillOperatorName())
            .machineId(request.getMachineId())
            .machineType(request.getMachineType())
            .status(request.getStatus())
            .notes(request.getNotes())
            .build();

        // Save the record (efficiency and wastage will be calculated by @PrePersist)
        record = threshingRepository.save(record);

        // If status is COMPLETED, update stocks
        if (request.getStatus() == ThreshingStatus.COMPLETED) {
            UUID riceStockId = processCompletedThreshing(record, paddyStock);
            record.setRiceStockId(riceStockId);
            record = threshingRepository.save(record);
        }

        log.info("Threshing record created successfully with batch number: {}", batchNumber);
        return mapToResponse(record);
    }

    /**
     * Get all threshing records with pagination
     */
    @Transactional(readOnly = true)
    public Page<ThreshingDto.Response> getAllThreshingRecords(int page, int size, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return threshingRepository.findByDeletedAtIsNull(pageable)
            .map(this::mapToResponse);
    }

    /**
     * Get threshing record by ID
     */
    @Transactional(readOnly = true)
    public ThreshingDto.Response getThreshingRecordById(UUID id) {
        ThreshingRecord record = threshingRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ThreshingRecordNotFoundException("Threshing record not found with ID: " + id));
        return mapToResponse(record);
    }

    /**
     * Get threshing record by batch number
     */
    @Transactional(readOnly = true)
    public ThreshingDto.Response getThreshingRecordByBatchNumber(String batchNumber) {
        ThreshingRecord record = threshingRepository.findByBatchNumberAndDeletedAtIsNull(batchNumber)
            .orElseThrow(() -> new ThreshingRecordNotFoundException("Threshing record not found with batch number: " + batchNumber));
        return mapToResponse(record);
    }

    /**
     * Update threshing record
     */
    @Transactional
    public ThreshingDto.Response updateThreshingRecord(UUID id, ThreshingDto.UpdateRequest request) {
        log.info("Updating threshing record with ID: {}", id);

        ThreshingRecord record = threshingRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ThreshingRecordNotFoundException("Threshing record not found with ID: " + id));

        // Cannot update completed records
        if (record.getStatus() == ThreshingStatus.COMPLETED) {
            throw new InvalidThreshingDataException("Cannot update completed threshing record. Please cancel it first.");
        }

        // Update fields if provided
        if (request.getPaddyVariety() != null) {
            record.setPaddyVariety(request.getPaddyVariety());
        }
        if (request.getInputPaddyQuantity() != null) {
            record.setInputPaddyQuantity(request.getInputPaddyQuantity());
        }
        if (request.getOutputRiceQuantity() != null) {
            record.setOutputRiceQuantity(request.getOutputRiceQuantity());
        }
        if (request.getThreshingDate() != null) {
            record.setThreshingDate(request.getThreshingDate());
        }
        if (request.getMillOperatorName() != null) {
            record.setMillOperatorName(request.getMillOperatorName());
        }
        if (request.getMachineId() != null) {
            record.setMachineId(request.getMachineId());
        }
        if (request.getMachineType() != null) {
            record.setMachineType(request.getMachineType());
        }
        if (request.getNotes() != null) {
            record.setNotes(request.getNotes());
        }
        if (request.getStatus() != null) {
            record.setStatus(request.getStatus());
        }

        // Validate updated data
        if (record.getOutputRiceQuantity().compareTo(record.getInputPaddyQuantity()) > 0) {
            throw new InvalidThreshingDataException("Output rice quantity cannot exceed input paddy quantity");
        }

        record = threshingRepository.save(record);
        log.info("Threshing record updated successfully with ID: {}", id);

        return mapToResponse(record);
    }

    /**
     * Delete threshing record (soft delete)
     */
    @Transactional
    public void deleteThreshingRecord(UUID id) {
        log.info("Deleting threshing record with ID: {}", id);

        ThreshingRecord record = threshingRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ThreshingRecordNotFoundException("Threshing record not found with ID: " + id));

        // Cannot delete completed records
        if (record.getStatus() == ThreshingStatus.COMPLETED) {
            throw new InvalidThreshingDataException("Cannot delete completed threshing record. Please cancel it first.");
        }

        record.setDeletedAt(LocalDateTime.now());
        record.setActive(false);
        threshingRepository.save(record);

        log.info("Threshing record deleted successfully with ID: {}", id);
    }

    /**
     * Update threshing status
     */
    @Transactional
    public ThreshingDto.Response updateThreshingStatus(UUID id, ThreshingStatus status) {
        log.info("Updating threshing status for ID: {} to {}", id, status);

        ThreshingRecord record = threshingRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ThreshingRecordNotFoundException("Threshing record not found with ID: " + id));

        ThreshingStatus oldStatus = record.getStatus();
        record.setStatus(status);

        // If changing to COMPLETED, update stocks
        if (status == ThreshingStatus.COMPLETED && oldStatus != ThreshingStatus.COMPLETED) {
            PaddyStock paddyStock = paddyStockRepository.findByIdAndDeletedAtIsNull(record.getPaddyStockId())
                .orElseThrow(() -> new StockNotFoundException("Paddy stock not found"));

            UUID riceStockId = processCompletedThreshing(record, paddyStock);
            record.setRiceStockId(riceStockId);
        }

        record = threshingRepository.save(record);
        log.info("Threshing status updated successfully for ID: {}", id);

        return mapToResponse(record);
    }

    /**
     * Get records by status
     */
    @Transactional(readOnly = true)
    public List<ThreshingDto.Response> getRecordsByStatus(ThreshingStatus status) {
        return threshingRepository.findByStatusAndDeletedAtIsNull(status)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get records by date range
     */
    @Transactional(readOnly = true)
    public List<ThreshingDto.Response> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return threshingRepository.findByThreshingDateBetweenAndDeletedAtIsNull(startDate, endDate)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get records by machine ID
     */
    @Transactional(readOnly = true)
    public List<ThreshingDto.Response> getRecordsByMachineId(String machineId) {
        return threshingRepository.findByMachineIdAndDeletedAtIsNull(machineId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get records by paddy variety
     */
    @Transactional(readOnly = true)
    public List<ThreshingDto.Response> getRecordsByPaddyVariety(String paddyVariety) {
        return threshingRepository.findByPaddyVarietyAndDeletedAtIsNull(paddyVariety)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get high efficiency records
     */
    @Transactional(readOnly = true)
    public List<ThreshingDto.Response> getHighEfficiencyRecords(Double minEfficiency) {
        return threshingRepository.findByMinimumEfficiency(minEfficiency)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get total threshed quantity by date range
     */
    @Transactional(readOnly = true)
    public Double getTotalThreshedQuantity(LocalDate startDate, LocalDate endDate) {
        return threshingRepository.getTotalThreshedQuantity(startDate, endDate);
    }

    /**
     * Get threshing summary by date range
     */
    @Transactional(readOnly = true)
    public ThreshingDto.Summary getThreshingSummary(LocalDate startDate, LocalDate endDate) {
        Long totalRecords = threshingRepository.countByDateRange(startDate, endDate);
        Double totalInput = threshingRepository.getTotalThreshedQuantity(startDate, endDate);
        Double totalOutput = threshingRepository.getTotalOutputRice(startDate, endDate);
        Double totalWastage = threshingRepository.getTotalWastage(startDate, endDate);
        Double avgEfficiency = threshingRepository.getAverageEfficiency(startDate, endDate);

        return ThreshingDto.Summary.builder()
            .totalRecords(totalRecords)
            .totalInputPaddy(BigDecimal.valueOf(totalInput))
            .totalOutputRice(BigDecimal.valueOf(totalOutput))
            .totalWastage(BigDecimal.valueOf(totalWastage))
            .averageEfficiency(avgEfficiency != null ? BigDecimal.valueOf(avgEfficiency) : BigDecimal.ZERO)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    // ============== PRIVATE HELPER METHODS ==============

    /**
     * Validate threshing request
     */
    private void validateThreshingRequest(ThreshingDto.CreateRequest request) {
        // Output quantity cannot exceed input quantity
        if (request.getOutputRiceQuantity().compareTo(request.getInputPaddyQuantity()) > 0) {
            throw new InvalidThreshingDataException("Output rice quantity cannot exceed input paddy quantity");
        }

        // Threshing date cannot be in the future
        if (request.getThreshingDate().isAfter(LocalDate.now())) {
            throw new InvalidThreshingDataException("Threshing date cannot be in the future");
        }

        // Calculate efficiency and warn if out of normal range
        BigDecimal efficiency = request.getOutputRiceQuantity()
            .divide(request.getInputPaddyQuantity(), 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"));

        if (efficiency.doubleValue() < minEfficiency) {
            log.warn("Low threshing efficiency detected: {}%. Expected minimum: {}%",
                efficiency, minEfficiency);
        } else if (efficiency.doubleValue() > maxEfficiency) {
            log.warn("Unusually high threshing efficiency detected: {}%. Expected maximum: {}%",
                efficiency, maxEfficiency);
        }
    }

    /**
     * Generate unique batch number
     */
    private String generateBatchNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = batchPrefix + "-" + dateStr + "-";

        Long count = threshingRepository.countByBatchNumberPrefix(prefix);
        String sequence = String.format("%04d", count + 1);

        return prefix + sequence;
    }

    /**
     * Process completed threshing - update stocks
     */
    private UUID processCompletedThreshing(ThreshingRecord record, PaddyStock paddyStock) {
        log.info("Processing completed threshing for batch: {}", record.getBatchNumber());

        // Deduct paddy quantity from paddy stock
        BigDecimal newPaddyQuantity = paddyStock.getQuantity().subtract(record.getInputPaddyQuantity());
        paddyStock.setQuantity(newPaddyQuantity);
        paddyStockRepository.save(paddyStock);
        log.info("Deducted {} kg from paddy stock. New quantity: {} kg",
            record.getInputPaddyQuantity(), newPaddyQuantity);

        // Create rice stock entry
        RiceStock riceStock = RiceStock.builder()
            .riceType(record.getPaddyVariety() + " Rice")
            .quantity(record.getOutputRiceQuantity())
            .unit("kg")
            .pricePerKg(paddyStock.getPricePerKg() != null ? paddyStock.getPricePerKg().multiply(new BigDecimal("1.5")) : BigDecimal.ZERO) // 50% markup
            .warehouseLocation(paddyStock.getWarehouse())
            .status("In Stock")
            .build();

        riceStock = riceStockRepository.save(riceStock);
        log.info("Created rice stock entry with {} kg", record.getOutputRiceQuantity());

        return riceStock.getId();
    }

    /**
     * Map entity to response DTO
     */
    private ThreshingDto.Response mapToResponse(ThreshingRecord record) {
        return ThreshingDto.Response.builder()
            .id(record.getId())
            .batchNumber(record.getBatchNumber())
            .paddyStockId(record.getPaddyStockId())
            .paddyVariety(record.getPaddyVariety())
            .inputPaddyQuantity(record.getInputPaddyQuantity())
            .outputRiceQuantity(record.getOutputRiceQuantity())
            .wastageQuantity(record.getWastageQuantity())
            .threshingEfficiency(record.getThreshingEfficiency())
            .threshingDate(record.getThreshingDate())
            .millOperatorName(record.getMillOperatorName())
            .machineId(record.getMachineId())
            .machineType(record.getMachineType())
            .status(record.getStatus())
            .notes(record.getNotes())
            .riceStockId(record.getRiceStockId())
            .createdAt(record.getCreatedAt())
            .updatedAt(record.getUpdatedAt())
            .createdBy(record.getCreatedBy())
            .updatedBy(record.getUpdatedBy())
            .build();
    }
}

