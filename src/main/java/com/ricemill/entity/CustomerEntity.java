package com.ricemill.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customer_contact", columnList = "contact"),
    @Index(name = "idx_customer_email", columnList = "email"),
    @Index(name = "idx_customer_type", columnList = "customer_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 20)
    private String contact;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", length = 20)
    private CustomerType customerType = CustomerType.RETAIL;

    @Column(name = "credit_limit", precision = 12, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "outstanding_balance", precision = 12, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<PaddySale> paddySales;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<RiceSale> riceSales;
}

