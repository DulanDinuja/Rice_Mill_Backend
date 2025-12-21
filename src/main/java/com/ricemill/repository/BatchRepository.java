package com.ricemill.repository;

import com.ricemill.entity.Batch;
import com.ricemill.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {
    Optional<Batch> findByTypeAndBatchCode(ProductType type, String batchCode);
    boolean existsByTypeAndBatchCode(ProductType type, String batchCode);
}
