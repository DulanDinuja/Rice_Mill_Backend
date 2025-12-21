package com.ricemill.service;

import com.ricemill.dto.DashboardDto;
import com.ricemill.entity.InventoryBalance;
import com.ricemill.entity.ProductType;
import com.ricemill.entity.StockMovement;
import com.ricemill.entity.Warehouse;
import com.ricemill.repository.InventoryBalanceRepository;
import com.ricemill.repository.SettingsRepository;
import com.ricemill.repository.StockMovementRepository;
import com.ricemill.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SettingsRepository settingsRepository;
    
    public DashboardDto.Response getDashboard() {
        BigDecimal lowStockThreshold = new BigDecimal(
                settingsRepository.findBySettingKey("lowStockThreshold")
                        .map(s -> s.getSettingValue())
                        .orElse("100")
        );
        
        return DashboardDto.Response.builder()
                .paddyStock(getStockSummary(ProductType.PADDY))
                .riceStock(getStockSummary(ProductType.RICE))
                .warehouseUtilization(getWarehouseUtilization())
                .lowStockAlerts(getLowStockAlerts(lowStockThreshold))
                .recentMovements(getRecentMovements())
                .build();
    }
    
    private DashboardDto.StockSummary getStockSummary(ProductType type) {
        BigDecimal total = inventoryBalanceRepository.sumQuantityByType(type);
        return DashboardDto.StockSummary.builder()
                .totalQuantity(total)
                .unit("KG")
                .build();
    }
    
    private List<DashboardDto.WarehouseUtilization> getWarehouseUtilization() {
        List<Warehouse> warehouses = warehouseRepository.findByDeletedAtIsNullAndActiveTrue();
        
        return warehouses.stream().map(warehouse -> {
            List<InventoryBalance> inventories = inventoryBalanceRepository
                    .findByWarehouseIdAndType(warehouse.getId(), ProductType.PADDY);
            
            inventories.addAll(inventoryBalanceRepository
                    .findByWarehouseIdAndType(warehouse.getId(), ProductType.RICE));
            
            BigDecimal currentStock = inventories.stream()
                    .map(InventoryBalance::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal utilizationPercent = warehouse.getCapacity().compareTo(BigDecimal.ZERO) > 0
                    ? currentStock.divide(warehouse.getCapacity(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
            
            return DashboardDto.WarehouseUtilization.builder()
                    .warehouseName(warehouse.getName())
                    .capacity(warehouse.getCapacity())
                    .currentStock(currentStock)
                    .utilizationPercent(utilizationPercent)
                    .build();
        }).collect(Collectors.toList());
    }
    
    private List<DashboardDto.LowStockAlert> getLowStockAlerts(BigDecimal threshold) {
        List<InventoryBalance> lowStock = inventoryBalanceRepository.findLowStock(threshold);
        
        return lowStock.stream().map(inventory ->
                DashboardDto.LowStockAlert.builder()
                        .warehouseName(inventory.getWarehouse().getName())
                        .batchCode(inventory.getBatch().getBatchCode())
                        .type(inventory.getType().name())
                        .quantity(inventory.getQuantity())
                        .threshold(threshold)
                        .build()
        ).collect(Collectors.toList());
    }
    
    private List<DashboardDto.RecentMovement> getRecentMovements() {
        List<StockMovement> movements = stockMovementRepository.findRecentMovements(PageRequest.of(0, 10));
        
        return movements.stream().map(movement ->
                DashboardDto.RecentMovement.builder()
                        .movementType(movement.getMovementType().name())
                        .type(movement.getType().name())
                        .quantity(movement.getQuantity())
                        .warehouseName(movement.getWarehouseTo() != null 
                                ? movement.getWarehouseTo().getName() 
                                : (movement.getWarehouseFrom() != null ? movement.getWarehouseFrom().getName() : "N/A"))
                        .performedAt(movement.getPerformedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .build()
        ).collect(Collectors.toList());
    }
}
