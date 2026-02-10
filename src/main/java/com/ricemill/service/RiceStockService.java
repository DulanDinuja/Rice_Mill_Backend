package com.ricemill.service;

import com.ricemill.dto.RiceStockDto;
import com.ricemill.entity.RiceStock;
import com.ricemill.entity.User;
import com.ricemill.exception.DuplicateBatchNumberException;
import com.ricemill.exception.InvalidStockDataException;
import com.ricemill.exception.StockNotFoundException;
import com.ricemill.repository.PaddyStockRepository;
import com.ricemill.repository.RiceStockRepository;
import com.ricemill.repository.UserRepository;
import com.ricemill.util.BatchNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiceStockService {

    private final RiceStockRepository riceStockRepository;
    private final UserRepository userRepository;
    private final BatchNumberGenerator batchNumberGenerator;

    @Transactional
    public RiceStockDto.Response addRiceStock(RiceStockDto.AddRequest request) {
        log.info("Adding new rice stock for type: {}", request.getRiceType());

        // Validate stock data
        validateStockData(request);

        // Generate unique batch number
        String batchNumber = generateUniqueBatchNumber();
        log.info("Generated batch number: {}", batchNumber);

        // Calculate total value
        BigDecimal totalValue = request.getQuantity().multiply(request.getPricePerKg());

        // Set defaults if not provided
        String unit = request.getUnit() != null ? request.getUnit() : "kg";
        String status = request.getStatus() != null ? request.getStatus() : "In Stock";

        // Build entity
        RiceStock riceStock = RiceStock.builder()
                .riceType(request.getRiceType())
                .quantity(request.getQuantity())
                .unit(unit)
                .warehouseLocation(request.getWarehouse())
                .qualityGrade(request.getGrade())
                .pricePerKg(request.getPricePerKg())
                .customerName(request.getCustomerName())
                .customerId(request.getCustomerId())
                .mobileNumber(request.getMobileNumber())
                .status(status)
                .batchNumber(batchNumber)
                .totalValue(totalValue)
                .build();

        // Save
        RiceStock saved = riceStockRepository.save(riceStock);
        log.info("Rice stock added successfully with ID: {} and batch: {}", saved.getId(), saved.getBatchNumber());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public RiceStockDto.Response getRiceStockById(UUID id) {
        log.info("Fetching rice stock by ID: {}", id);
        RiceStock stock = riceStockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Rice stock not found with ID: " + id));
        return toResponse(stock);
    }

    @Transactional(readOnly = true)
    public RiceStockDto.Response getRiceStockByBatchNumber(String batchNumber) {
        log.info("Fetching rice stock by batch number: {}", batchNumber);
        RiceStock stock = riceStockRepository.findByBatchNumber(batchNumber)
                .orElseThrow(() -> new StockNotFoundException("Rice stock not found with batch number: " + batchNumber));
        return toResponse(stock);
    }

    @Transactional(readOnly = true)
    public Page<RiceStockDto.Response> getAllRiceStocks(String riceType, String warehouseLocation, Pageable pageable) {
        log.info("Fetching all rice stocks with filters - type: {}, warehouse: {}", riceType, warehouseLocation);
        Page<RiceStock> stocks = riceStockRepository.findByFilters(riceType, warehouseLocation, pageable);
        return stocks.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public RiceStockDto.SummaryResponse getSummary() {
        log.info("Fetching rice stock summary");
        Long count = riceStockRepository.count();
        Double totalQuantity = riceStockRepository.getTotalQuantity();
        Double totalValue = riceStockRepository.getTotalValue();

        return RiceStockDto.SummaryResponse.builder()
                .totalStocks(count)
                .totalQuantity(totalQuantity != null ? BigDecimal.valueOf(totalQuantity) : BigDecimal.ZERO)
                .totalValue(totalValue != null ? BigDecimal.valueOf(totalValue) : BigDecimal.ZERO)
                .unit("KG")
                .build();
    }

    private void validateStockData(RiceStockDto.AddRequest request) {
        if (request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStockDataException("Quantity must be greater than 0");
        }

        if (request.getPricePerKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStockDataException("Price per kg must be greater than 0");
        }
    }

    private String generateUniqueBatchNumber() {
        String batchNumber;
        int attempts = 0;
        do {
            batchNumber = batchNumberGenerator.generateRiceBatchNumber();
            attempts++;
            if (attempts > 100) {
                throw new InvalidStockDataException("Unable to generate unique batch number after 100 attempts");
            }
        } while (riceStockRepository.existsByBatchNumber(batchNumber));

        return batchNumber;
    }

    private RiceStockDto.Response toResponse(RiceStock stock) {
        String createdByName = null;
        if (stock.getCreatedBy() != null) {
            createdByName = userRepository.findById(stock.getCreatedBy())
                    .map(User::getUsername)
                    .orElse("Unknown");
        }

        return RiceStockDto.Response.builder()
                .id(stock.getId())
                .riceType(stock.getRiceType())
                .quantity(stock.getQuantity())
                .unit(stock.getUnit())
                .warehouseLocation(stock.getWarehouseLocation())
                .qualityGrade(stock.getQualityGrade())
                .pricePerKg(stock.getPricePerKg())
                .customerName(stock.getCustomerName())
                .customerId(stock.getCustomerId())
                .mobileNumber(stock.getMobileNumber())
                .status(stock.getStatus())
                .batchNumber(stock.getBatchNumber())
                .totalValue(stock.getTotalValue())
                .createdAt(stock.getCreatedAt())
                .createdBy(createdByName)
                .build();
    }
}

