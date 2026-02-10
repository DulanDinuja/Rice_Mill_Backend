package com.ricemill.repository;

import com.ricemill.entity.PaddyStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaddyStockRepository extends JpaRepository<PaddyStock, UUID> {

    Optional<PaddyStock> findByIdAndDeletedAtIsNull(UUID id);

    Page<PaddyStock> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT ps FROM PaddyStock ps WHERE ps.deletedAt IS NULL " +
           "AND (:paddyType IS NULL OR ps.paddyType = :paddyType) " +
           "AND (:warehouse IS NULL OR ps.warehouse = :warehouse) " +
           "ORDER BY ps.createdAt DESC")
    Page<PaddyStock> findByFilters(@Param("paddyType") String paddyType,
                                    @Param("warehouse") String warehouse,
                                    Pageable pageable);

    @Query("SELECT SUM(ps.quantity) FROM PaddyStock ps WHERE ps.deletedAt IS NULL")
    Double getTotalQuantity();

    @Query("SELECT SUM(ps.totalValue) FROM PaddyStock ps WHERE ps.deletedAt IS NULL")
    Double getTotalValue();
}

