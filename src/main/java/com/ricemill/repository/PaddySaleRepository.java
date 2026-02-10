package com.ricemill.repository;

import com.ricemill.entity.PaddySale;
import com.ricemill.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaddySaleRepository extends JpaRepository<PaddySale, UUID> {

    Optional<PaddySale> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM PaddySale ps WHERE ps.id = :id")
    Optional<PaddySale> findByIdWithLock(@Param("id") UUID id);

    Page<PaddySale> findByDeletedAtIsNullOrderBySaleDateDesc(Pageable pageable);

    @Query("SELECT ps FROM PaddySale ps WHERE ps.deletedAt IS NULL " +
           "AND (:customerId IS NULL OR ps.customer.id = :customerId) " +
           "AND (:paymentStatus IS NULL OR ps.paymentStatus = :paymentStatus) " +
           "AND (:startDate IS NULL OR ps.saleDate >= :startDate) " +
           "AND (:endDate IS NULL OR ps.saleDate <= :endDate) " +
           "ORDER BY ps.saleDate DESC")
    Page<PaddySale> findByFilters(@Param("customerId") UUID customerId,
                                   @Param("paymentStatus") PaymentStatus paymentStatus,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   Pageable pageable);

    @Query("SELECT SUM(ps.totalAmount) FROM PaddySale ps " +
           "WHERE ps.deletedAt IS NULL " +
           "AND ps.saleDate BETWEEN :startDate AND :endDate")
    Double getTotalSalesAmount(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(ps) FROM PaddySale ps " +
           "WHERE ps.deletedAt IS NULL " +
           "AND ps.paymentStatus = :status")
    Long countByPaymentStatus(@Param("status") PaymentStatus status);
}

