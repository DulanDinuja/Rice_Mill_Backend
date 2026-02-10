package com.ricemill.service;

import com.ricemill.dto.PaddyStockDto;
import com.ricemill.entity.PaddyStock;
import com.ricemill.entity.User;
import com.ricemill.exception.InvalidStockDataException;
import com.ricemill.exception.StockNotFoundException;
import com.ricemill.repository.PaddyStockRepository;
import com.ricemill.repository.UserRepository;
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
public class PaddyStockService {

    private final PaddyStockRepository paddyStockRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaddyStockDto.Response addPaddyStock(PaddyStockDto.AddRequest request) {
        log.info("Adding new paddy stock for type: {}", request.getPaddyType());

        // Validate stock data
        validateStockData(request);

        // Calculate total value if pricePerKg is provided
        BigDecimal totalValue = null;
        if (request.getPricePerKg() != null && request.getQuantity() != null) {
            totalValue = request.getQuantity().multiply(request.getPricePerKg());
        }

        // Build entity
        PaddyStock paddyStock = PaddyStock.builder()
                .paddyType(request.getPaddyType())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .warehouse(request.getWarehouse())
                .moistureLevel(request.getMoistureLevel())
                .supplier(request.getSupplier())
                .pricePerKg(request.getPricePerKg())
                .customerName(request.getCustomerName())
                .customerId(request.getCustomerId())
                .mobileNumber(request.getMobileNumber())
                .status(request.getStatus() != null ? request.getStatus() : "In Stock")
                .totalValue(totalValue)
                .build();

        // Save
        PaddyStock saved = paddyStockRepository.save(paddyStock);
        log.info("Paddy stock added successfully with ID: {}", saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaddyStockDto.Response getPaddyStockById(UUID id) {
        log.info("Fetching paddy stock by ID: {}", id);
        PaddyStock stock = paddyStockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Paddy stock not found with ID: " + id));
        return toResponse(stock);
    }

    @Transactional(readOnly = true)
    public Page<PaddyStockDto.Response> getAllPaddyStocks(String paddyType, String warehouse, Pageable pageable) {
        log.info("Fetching all paddy stocks with filters - type: {}, warehouse: {}", paddyType, warehouse);
        Page<PaddyStock> stocks = paddyStockRepository.findByFilters(paddyType, warehouse, pageable);
        return stocks.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PaddyStockDto.SummaryResponse getSummary() {
        log.info("Fetching paddy stock summary");
        Long count = paddyStockRepository.count();
        Double totalQuantity = paddyStockRepository.getTotalQuantity();
        Double totalValue = paddyStockRepository.getTotalValue();

        return PaddyStockDto.SummaryResponse.builder()
                .totalStocks(count)
                .totalQuantity(totalQuantity != null ? BigDecimal.valueOf(totalQuantity) : BigDecimal.ZERO)
                .totalValue(totalValue != null ? BigDecimal.valueOf(totalValue) : BigDecimal.ZERO)
                .unit("KG")
                .build();
    }

    private void validateStockData(PaddyStockDto.AddRequest request) {
        if (request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStockDataException("Quantity must be greater than 0");
        }

        if (request.getPricePerKg() != null && request.getPricePerKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStockDataException("Price per kg must be greater than 0");
        }

        if (request.getMoistureLevel() != null) {
            if (request.getMoistureLevel().compareTo(BigDecimal.ZERO) < 0 ||
                request.getMoistureLevel().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new InvalidStockDataException("Moisture level must be between 0 and 100");
            }
        }
    }

    private PaddyStockDto.Response toResponse(PaddyStock stock) {
        String createdByName = null;
        if (stock.getCreatedBy() != null) {
            createdByName = userRepository.findById(stock.getCreatedBy())
                    .map(User::getUsername)
                    .orElse("Unknown");
        }

        return PaddyStockDto.Response.builder()
                .id(stock.getId())
                .paddyType(stock.getPaddyType())
                .quantity(stock.getQuantity())
                .unit(stock.getUnit())
                .warehouse(stock.getWarehouse())
                .moistureLevel(stock.getMoistureLevel())
                .supplier(stock.getSupplier())
                .pricePerKg(stock.getPricePerKg())
                .customerName(stock.getCustomerName())
                .customerId(stock.getCustomerId())
                .mobileNumber(stock.getMobileNumber())
                .status(stock.getStatus())
                .totalValue(stock.getTotalValue())
                .createdAt(stock.getCreatedAt())
                .createdBy(createdByName)
                .build();
    }
}

