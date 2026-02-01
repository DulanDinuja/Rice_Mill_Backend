package com.ricemill.service;

import com.ricemill.dto.StockDto;
import com.ricemill.entity.*;
import com.ricemill.exception.BusinessException;
import com.ricemill.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StockServiceTest {
    
    @Mock
    private InventoryBalanceRepository inventoryBalanceRepository;
    
    @Mock
    private StockMovementRepository stockMovementRepository;
    
    @Mock
    private WarehouseRepository warehouseRepository;
    
    @Mock
    private BatchRepository batchRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SupplierRepository supplierRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private ProcessingRecordRepository processingRecordRepository;
    
    @InjectMocks
    private StockService stockService;
    
    private Warehouse warehouse;
    private Batch batch;
    private User user;
    private InventoryBalance inventory;
    
    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder()
                .name("Main Warehouse")
                .capacity(BigDecimal.valueOf(10000))
                .build();
        warehouse.setId(UUID.randomUUID());
        warehouse.setActive(true);
        
        batch = Batch.builder()
                .type(ProductType.PADDY)
                .batchCode("BATCH001")
                .batchDate(LocalDate.now())
                .build();
        batch.setId(UUID.randomUUID());
        batch.setActive(true);
        
        user = User.builder()
                .username("testuser")
                .fullName("Test User")
                .build();
        user.setId(UUID.randomUUID());
        user.setActive(true);
        
        inventory = InventoryBalance.builder()
                .warehouse(warehouse)
                .batch(batch)
                .type(ProductType.PADDY)
                .quantity(BigDecimal.valueOf(100))
                .unit("KG")
                .build();
        inventory.setId(UUID.randomUUID());
        
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        // Ensure StockService#getCurrentUser() can resolve the authenticated username.
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }
    
    @Test
    void shouldPreventNegativeStockOnOutbound() {
        StockDto.OutboundRequest request = new StockDto.OutboundRequest();
        request.setType(ProductType.PADDY);
        request.setWarehouseId(warehouse.getId());
        request.setBatchId(batch.getId());
        request.setQuantity(BigDecimal.valueOf(200));
        request.setAdminOverride(false);
        
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouse.getId()))
                .thenReturn(Optional.of(warehouse));
        when(batchRepository.findById(batch.getId()))
                .thenReturn(Optional.of(batch));
        when(inventoryBalanceRepository.findByWarehouseAndBatchAndTypeForUpdate(warehouse, batch, ProductType.PADDY))
                .thenReturn(Optional.of(inventory));
        
        assertThatThrownBy(() -> stockService.outbound(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");
        
        verify(inventoryBalanceRepository, never()).save(any());
    }
    
    @Test
    void shouldAllowNegativeStockWithAdminOverride() {
        StockDto.OutboundRequest request = new StockDto.OutboundRequest();
        request.setType(ProductType.PADDY);
        request.setWarehouseId(warehouse.getId());
        request.setBatchId(batch.getId());
        request.setQuantity(BigDecimal.valueOf(200));
        request.setAdminOverride(true);
        
        when(warehouseRepository.findByIdAndDeletedAtIsNull(warehouse.getId()))
                .thenReturn(Optional.of(warehouse));
        when(batchRepository.findById(batch.getId()))
                .thenReturn(Optional.of(batch));
        when(inventoryBalanceRepository.findByWarehouseAndBatchAndTypeForUpdate(warehouse, batch, ProductType.PADDY))
                .thenReturn(Optional.of(inventory));
        when(inventoryBalanceRepository.save(any())).thenReturn(inventory);
        
        stockService.outbound(request);
        
        verify(inventoryBalanceRepository).save(any());
        verify(stockMovementRepository).save(any());
    }
    
    @Test
    void shouldPreventTransferToSameWarehouse() {
        StockDto.TransferRequest request = new StockDto.TransferRequest();
        request.setType(ProductType.PADDY);
        request.setFromWarehouseId(warehouse.getId());
        request.setToWarehouseId(warehouse.getId());
        request.setBatchId(batch.getId());
        request.setQuantity(BigDecimal.valueOf(50));
        
        assertThatThrownBy(() -> stockService.transfer(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must be different");
    }
}
