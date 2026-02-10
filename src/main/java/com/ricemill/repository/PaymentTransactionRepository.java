package com.ricemill.repository;

import com.ricemill.entity.PaymentTransaction;
import com.ricemill.entity.SaleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    @Query("SELECT pt FROM PaymentTransaction pt " +
           "WHERE pt.saleId = :saleId AND pt.saleType = :saleType " +
           "ORDER BY pt.paymentDate DESC")
    List<PaymentTransaction> findBySaleIdAndSaleType(@Param("saleId") UUID saleId,
                                                       @Param("saleType") SaleType saleType);

    Page<PaymentTransaction> findByDeletedAtIsNullOrderByPaymentDateDesc(Pageable pageable);
}

