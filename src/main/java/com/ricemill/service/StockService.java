package com.ricemill.service;

import com.ricemill.dto.StockDto;
import com.ricemill.entity.*;
import com.ricemill.exception.BusinessException;
import com.ricemill.exception.ResourceNotFoundException;
import com.ricemill.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProcessingRecordRepository processingRecordRepository;
    private final WarehouseRepository warehouseRepository;
    private final BatchRepository batchRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    
    public Page<StockDto.InventoryResponse> getStocks(ProductType type, UUID warehouseId, Pageable pageable) {
        return inventoryBalanceRepository.findByTypeAndWarehouse(type, warehouseId, pageable)
                .map(this::toInventoryResponse);
    }
    
    public StockDto.SummaryResponse getSummary(ProductType type) {
        BigDecimal total = inventoryBalanceRepository.sumQuantityByType(type);
        return StockDto.SummaryResponse.builder()
                .type(type)
                .totalQuantity(total)
                .unit("KG")
                .build();
    }
    
    @Transactional
    public StockDto.InventoryResponse inbound(StockDto.InboundRequest request) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        
        Batch batch = batchRepository.findByTypeAndBatchCode(request.getType(), request.getBatchCode())
                .orElseGet(() -> createBatch(request));
        
        InventoryBalance inventory = inventoryBalanceRepository
                .findByWarehouseAndBatchAndTypeForUpdate(warehouse, batch, request.getType())
                .orElseGet(() -> createInventoryBalance(warehouse, batch, request.getType()));
        
        inventory.setQuantity(inventory.getQuantity().add(request.getQuantity()));
        inventory = inventoryBalanceRepository.save(inventory);
        
        createStockMovement(MovementType.INBOUND, request.getType(), request.getQuantity(), 
                null, warehouse, batch, request.getSupplierId(), null, request.getReferenceNo(), request.getNotes());
        
        return toInventoryResponse(inventory);
    }
    
    @Transactional
    public void outbound(StockDto.OutboundRequest request) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        
        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        
        InventoryBalance inventory = inventoryBalanceRepository
                .findByWarehouseAndBatchAndTypeForUpdate(warehouse, batch, request.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        
        BigDecimal newQuantity = inventory.getQuantity().subtract(request.getQuantity());
        
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0 && !request.getAdminOverride()) {
            throw new BusinessException("Insufficient stock. Available: " + inventory.getQuantity());
        }
        
        inventory.setQuantity(newQuantity);
        inventoryBalanceRepository.save(inventory);
        
        createStockMovement(MovementType.OUTBOUND, request.getType(), request.getQuantity(), 
                warehouse, null, batch, null, request.getCustomerId(), request.getReferenceNo(), request.getReason());
    }
    
    @Transactional
    public void transfer(StockDto.TransferRequest request) {
        if (request.getFromWarehouseId().equals(request.getToWarehouseId())) {
            throw new BusinessException("Source and destination warehouses must be different");
        }
        
        Warehouse fromWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(request.getFromWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Source warehouse not found"));
        
        Warehouse toWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(request.getToWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination warehouse not found"));
        
        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        
        InventoryBalance fromInventory = inventoryBalanceRepository
                .findByWarehouseAndBatchAndTypeForUpdate(fromWarehouse, batch, request.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Source inventory not found"));
        
        BigDecimal newFromQuantity = fromInventory.getQuantity().subtract(request.getQuantity());
        if (newFromQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Insufficient stock in source warehouse");
        }
        
        fromInventory.setQuantity(newFromQuantity);
        inventoryBalanceRepository.save(fromInventory);
        
        InventoryBalance toInventory = inventoryBalanceRepository
                .findByWarehouseAndBatchAndTypeForUpdate(toWarehouse, batch, request.getType())
                .orElseGet(() -> createInventoryBalance(toWarehouse, batch, request.getType()));
        
        toInventory.setQuantity(toInventory.getQuantity().add(request.getQuantity()));
        inventoryBalanceRepository.save(toInventory);
        
        createStockMovement(MovementType.TRANSFER, request.getType(), request.getQuantity(), 
                fromWarehouse, toWarehouse, batch, null, null, request.getReferenceNo(), request.getReason());
    }
    
    @Transactional
    public void adjust(StockDto.AdjustmentRequest request) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        
        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        
        InventoryBalance inventory = inventoryBalanceRepository
                .findByWarehouseAndBatchAndTypeForUpdate(warehouse, batch, request.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        
        BigDecimal newQuantity = inventory.getQuantity().add(request.getQuantity());
        
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0 && !request.getAdminOverride()) {
            throw new BusinessException("Adjustment would result in negative stock");
        }
        
        inventory.setQuantity(newQuantity);
        inventoryBalanceRepository.save(inventory);
        
        createStockMovement(MovementType.ADJUSTMENT, request.getType(), request.getQuantity(), 
                warehouse, warehouse, batch, null, null, request.getReferenceNo(), request.getReason());
    }
    
    @Transactional
    public void process(StockDto.ProcessRequest request) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        
        Batch inputBatch = batchRepository.findById(request.getInputBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Input batch not found"));
        
        if (inputBatch.getType() != ProductType.PADDY) {
            throw new BusinessException("Input batch must be PADDY type");
        }
        
        InventoryBalance paddyInventory = inventoryBalanceRepository
                .findByWarehouseAndBatchAndTypeForUpdate(warehouse, inputBatch, ProductType.PADDY)
                .orElseThrow(() -> new ResourceNotFoundException("Paddy inventory not found"));
        
        BigDecimal newPaddyQty = paddyInventory.getQuantity().subtract(request.getInputQty());
        if (newPaddyQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Insufficient paddy stock");
        }
        
        paddyInventory.setQuantity(newPaddyQty);
        inventoryBalanceRepository.save(paddyInventory);
        
        Batch outputBatch = batchRepository.findByTypeAndBatchCode(ProductType.RICE, request.getOutputBatchCode())
                .orElseGet(() -> {
                    Batch newBatch = Batch.builder()
                            .type(ProductType.RICE)
                            .batchCode(request.getOutputBatchCode())
                            .batchDate(request.getOutputBatchDate())
                            .variety(request.getOutputVariety())
//                            .active(true)
                            .build();
                    return batchRepository.save(newBatch);
                });
        
        InventoryBalance riceInventory = inventoryBalanceRepository
                .findByWarehouseAndBatchAndTypeForUpdate(warehouse, outputBatch, ProductType.RICE)
                .orElseGet(() -> createInventoryBalance(warehouse, outputBatch, ProductType.RICE));
        
        riceInventory.setQuantity(riceInventory.getQuantity().add(request.getOutputQty()));
        inventoryBalanceRepository.save(riceInventory);
        
        BigDecimal yieldPercent = request.getOutputQty()
                .divide(request.getInputQty(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        User currentUser = getCurrentUser();
        
        ProcessingRecord record = ProcessingRecord.builder()
                .inputBatch(inputBatch)
                .outputBatch(outputBatch)
                .warehouse(warehouse)
                .inputQty(request.getInputQty())
                .outputQty(request.getOutputQty())
                .wasteQty(request.getWasteQty())
                .yieldPercent(yieldPercent)
                .referenceNo(request.getReferenceNo())
                .notes(request.getNotes())
                .performedBy(currentUser)
                .performedAt(LocalDateTime.now())
                .build();
        
        processingRecordRepository.save(record);
        
        createStockMovement(MovementType.PROCESSING, ProductType.PADDY, request.getInputQty(), 
                warehouse, null, inputBatch, null, null, request.getReferenceNo(), "Processing to rice");
        
        createStockMovement(MovementType.PROCESSING, ProductType.RICE, request.getOutputQty(), 
                null, warehouse, outputBatch, null, null, request.getReferenceNo(), "Processed from paddy");
    }
    
    private Batch createBatch(StockDto.InboundRequest request) {
        Batch batch = Batch.builder()
                .type(request.getType())
                .batchCode(request.getBatchCode())
                .batchDate(request.getBatchDate())
                .variety(request.getVariety())
                .moisture(request.getMoisture())
//                .active(true)
                .build();
        
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findByIdAndDeletedAtIsNull(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
            batch.setSupplier(supplier);
        }
        
        return batchRepository.save(batch);
    }
    
    private InventoryBalance createInventoryBalance(Warehouse warehouse, Batch batch, ProductType type) {
        return InventoryBalance.builder()
                .warehouse(warehouse)
                .batch(batch)
                .type(type)
                .quantity(BigDecimal.ZERO)
                .unit("KG")
//                .active(true)
                .build();
    }
    
    private void createStockMovement(MovementType movementType, ProductType type, BigDecimal quantity,
                                     Warehouse from, Warehouse to, Batch batch, UUID supplierId, UUID customerId,
                                     String referenceNo, String reason) {
        User currentUser = getCurrentUser();
        
        StockMovement.StockMovementBuilder builder = StockMovement.builder()
                .movementType(movementType)
                .type(type)
                .quantity(quantity)
                .unit("KG")
                .warehouseFrom(from)
                .warehouseTo(to)
                .batch(batch)
                .referenceNo(referenceNo)
                .reason(reason)
                .performedBy(currentUser)
                .performedAt(LocalDateTime.now());
        
        if (supplierId != null) {
            Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
            builder.supplier(supplier);
        }
        
        if (customerId != null) {
            CustomerEntity customer = customerRepository.findById(customerId).orElse(null);
            builder.customer(customer);
        }
        
        stockMovementRepository.save(builder.build());
    }
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
    
    private StockDto.InventoryResponse toInventoryResponse(InventoryBalance inventory) {
        return StockDto.InventoryResponse.builder()
                .id(inventory.getId())
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseName(inventory.getWarehouse().getName())
                .batchId(inventory.getBatch().getId())
                .batchCode(inventory.getBatch().getBatchCode())
                .type(inventory.getType())
                .quantity(inventory.getQuantity())
                .unit(inventory.getUnit())
                .variety(inventory.getBatch().getVariety())
                .batchDate(inventory.getBatch().getBatchDate())
                .build();
    }
}
