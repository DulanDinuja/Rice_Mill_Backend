package com.ricemill.repository;

import com.ricemill.entity.Batch;
import com.ricemill.entity.InventoryBalance;
import com.ricemill.entity.ProductType;
import com.ricemill.entity.Warehouse;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryBalanceRepository extends JpaRepository<InventoryBalance, UUID> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ib FROM InventoryBalance ib WHERE ib.warehouse = :warehouse AND ib.batch = :batch AND ib.type = :type")
    Optional<InventoryBalance> findByWarehouseAndBatchAndTypeForUpdate(
        @Param("warehouse") Warehouse warehouse,
        @Param("batch") Batch batch,
        @Param("type") ProductType type
    );
    
    Optional<InventoryBalance> findByWarehouseAndBatchAndType(Warehouse warehouse, Batch batch, ProductType type);
    
    @Query("SELECT ib FROM InventoryBalance ib WHERE ib.type = :type AND (:warehouseId IS NULL OR ib.warehouse.id = :warehouseId)")
    Page<InventoryBalance> findByTypeAndWarehouse(@Param("type") ProductType type, @Param("warehouseId") UUID warehouseId, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(ib.quantity), 0) FROM InventoryBalance ib WHERE ib.type = :type")
    BigDecimal sumQuantityByType(@Param("type") ProductType type);
    
    @Query("SELECT ib FROM InventoryBalance ib WHERE ib.warehouse.id = :warehouseId AND ib.type = :type")
    List<InventoryBalance> findByWarehouseIdAndType(@Param("warehouseId") UUID warehouseId, @Param("type") ProductType type);
    
    @Query("SELECT ib FROM InventoryBalance ib WHERE ib.quantity < :threshold")
    List<InventoryBalance> findLowStock(@Param("threshold") BigDecimal threshold);
}
