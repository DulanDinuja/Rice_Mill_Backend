package com.ricemill.repository;

import com.ricemill.entity.RiceStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiceStockRepository extends JpaRepository<RiceStock, UUID> {

    Optional<RiceStock> findByBatchNumber(String batchNumber);

    boolean existsByBatchNumber(String batchNumber);

    Optional<RiceStock> findByIdAndDeletedAtIsNull(UUID id);

    Page<RiceStock> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT rs FROM RiceStock rs WHERE rs.deletedAt IS NULL " +
           "AND (:riceType IS NULL OR rs.riceType = :riceType) " +
           "AND (:warehouseLocation IS NULL OR rs.warehouseLocation = :warehouseLocation) " +
           "ORDER BY rs.createdAt DESC")
    Page<RiceStock> findByFilters(@Param("riceType") String riceType,
                                   @Param("warehouseLocation") String warehouseLocation,
                                   Pageable pageable);

    @Query("SELECT SUM(rs.quantity) FROM RiceStock rs WHERE rs.deletedAt IS NULL")
    Double getTotalQuantity();

    @Query("SELECT SUM(rs.totalValue) FROM RiceStock rs WHERE rs.deletedAt IS NULL")
    Double getTotalValue();
}

