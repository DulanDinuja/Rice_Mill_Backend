package com.ricemill.repository;

import com.ricemill.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    Optional<Supplier> findByIdAndDeletedAtIsNull(UUID id);
    Page<Supplier> findByDeletedAtIsNull(Pageable pageable);
    List<Supplier> findByDeletedAtIsNullAndActiveTrue();
}
