package com.ricemill.repository;

import com.ricemill.entity.MovementType;
import com.ricemill.entity.ProductType;
import com.ricemill.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    
    @Query("SELECT sm FROM StockMovement sm WHERE " +
           "(:type IS NULL OR sm.type = :type) AND " +
           "(:movementType IS NULL OR sm.movementType = :movementType) AND " +
           "(:warehouseId IS NULL OR sm.warehouseFrom.id = :warehouseId OR sm.warehouseTo.id = :warehouseId) AND " +
           "(:from IS NULL OR sm.performedAt >= :from) AND " +
           "(:to IS NULL OR sm.performedAt <= :to)")
    Page<StockMovement> findByFilters(
        @Param("type") ProductType type,
        @Param("movementType") MovementType movementType,
        @Param("warehouseId") UUID warehouseId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );
    
    @Query("SELECT sm FROM StockMovement sm ORDER BY sm.performedAt DESC")
    List<StockMovement> findRecentMovements(Pageable pageable);
}
