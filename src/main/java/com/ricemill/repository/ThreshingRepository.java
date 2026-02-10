package com.ricemill.repository;

import com.ricemill.entity.ThreshingRecord;
import com.ricemill.entity.ThreshingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreshingRepository extends JpaRepository<ThreshingRecord, UUID> {

    // Find by batch number
    Optional<ThreshingRecord> findByBatchNumber(String batchNumber);

    // Find by batch number and not deleted
    Optional<ThreshingRecord> findByBatchNumberAndDeletedAtIsNull(String batchNumber);

    // Find by status
    List<ThreshingRecord> findByStatusAndDeletedAtIsNull(ThreshingStatus status);

    // Find by date range
    List<ThreshingRecord> findByThreshingDateBetweenAndDeletedAtIsNull(LocalDate startDate, LocalDate endDate);

    // Find by machine ID
    List<ThreshingRecord> findByMachineIdAndDeletedAtIsNull(String machineId);

    // Find by paddy variety
    List<ThreshingRecord> findByPaddyVarietyAndDeletedAtIsNull(String paddyVariety);

    // Find by operator name
    List<ThreshingRecord> findByMillOperatorNameAndDeletedAtIsNull(String operatorName);

    // Find all with pagination (not deleted)
    Page<ThreshingRecord> findByDeletedAtIsNull(Pageable pageable);

    // Find by ID and not deleted
    Optional<ThreshingRecord> findByIdAndDeletedAtIsNull(UUID id);

    // Custom query for efficiency reports
    @Query("SELECT t FROM ThreshingRecord t WHERE t.threshingEfficiency >= :minEfficiency AND t.deletedAt IS NULL ORDER BY t.threshingEfficiency DESC")
    List<ThreshingRecord> findByMinimumEfficiency(@Param("minEfficiency") Double minEfficiency);

    // Get total threshed quantity by date range
    @Query("SELECT COALESCE(SUM(t.inputPaddyQuantity), 0) FROM ThreshingRecord t WHERE t.threshingDate BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED' AND t.deletedAt IS NULL")
    Double getTotalThreshedQuantity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get count of records by batch number prefix for auto-generation
    @Query("SELECT COUNT(t) FROM ThreshingRecord t WHERE t.batchNumber LIKE :prefix%")
    Long countByBatchNumberPrefix(@Param("prefix") String prefix);

    // Get average efficiency by date range
    @Query("SELECT AVG(t.threshingEfficiency) FROM ThreshingRecord t WHERE t.threshingDate BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED' AND t.deletedAt IS NULL")
    Double getAverageEfficiency(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get total output rice by date range
    @Query("SELECT COALESCE(SUM(t.outputRiceQuantity), 0) FROM ThreshingRecord t WHERE t.threshingDate BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED' AND t.deletedAt IS NULL")
    Double getTotalOutputRice(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get total wastage by date range
    @Query("SELECT COALESCE(SUM(t.wastageQuantity), 0) FROM ThreshingRecord t WHERE t.threshingDate BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED' AND t.deletedAt IS NULL")
    Double getTotalWastage(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Count records by date range
    @Query("SELECT COUNT(t) FROM ThreshingRecord t WHERE t.threshingDate BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED' AND t.deletedAt IS NULL")
    Long countByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

