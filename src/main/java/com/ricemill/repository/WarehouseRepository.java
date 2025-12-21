package com.ricemill.repository;

import com.ricemill.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    Optional<Warehouse> findByIdAndDeletedAtIsNull(UUID id);
    Page<Warehouse> findByDeletedAtIsNull(Pageable pageable);
    List<Warehouse> findByDeletedAtIsNullAndActiveTrue();
    boolean existsByNameAndDeletedAtIsNull(String name);
}
