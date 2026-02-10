package com.ricemill.repository;

import com.ricemill.entity.RiceSale;
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
public interface RiceSaleRepository extends JpaRepository<RiceSale, UUID> {

    Optional<RiceSale> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rs FROM RiceSale rs WHERE rs.id = :id")
    Optional<RiceSale> findByIdWithLock(@Param("id") UUID id);

    Page<RiceSale> findByDeletedAtIsNullOrderBySaleDateDesc(Pageable pageable);

    @Query("SELECT rs FROM RiceSale rs WHERE rs.deletedAt IS NULL " +
           "AND (:customerId IS NULL OR rs.customer.id = :customerId) " +
           "AND (:paymentStatus IS NULL OR rs.paymentStatus = :paymentStatus) " +
           "AND (:startDate IS NULL OR rs.saleDate >= :startDate) " +
           "AND (:endDate IS NULL OR rs.saleDate <= :endDate) " +
           "ORDER BY rs.saleDate DESC")
    Page<RiceSale> findByFilters(@Param("customerId") UUID customerId,
                                  @Param("paymentStatus") PaymentStatus paymentStatus,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  Pageable pageable);

    @Query("SELECT SUM(rs.totalAmount) FROM RiceSale rs " +
           "WHERE rs.deletedAt IS NULL " +
           "AND rs.saleDate BETWEEN :startDate AND :endDate")
    Double getTotalSalesAmount(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(rs) FROM RiceSale rs " +
           "WHERE rs.deletedAt IS NULL " +
           "AND rs.paymentStatus = :status")
    Long countByPaymentStatus(@Param("status") PaymentStatus status);
}

