package com.ricemill.repository;

import com.ricemill.entity.ProcessingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProcessingRecordRepository extends JpaRepository<ProcessingRecord, UUID> {
    
    @Query("SELECT pr FROM ProcessingRecord pr WHERE " +
           "(:from IS NULL OR pr.performedAt >= :from) AND " +
           "(:to IS NULL OR pr.performedAt <= :to)")
    List<ProcessingRecord> findByDateRange(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
}
