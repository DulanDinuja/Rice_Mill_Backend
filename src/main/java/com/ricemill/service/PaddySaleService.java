package com.ricemill.service;

import com.ricemill.dto.PaddySaleDto;
import com.ricemill.entity.*;
import com.ricemill.exception.*;
import com.ricemill.repository.*;
import com.ricemill.util.CalculationUtil;
import com.ricemill.util.InvoiceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaddySaleService {

    private final PaddySaleRepository paddySaleRepository;
    private final PaddyStockRepository paddyStockRepository;
    private final CustomerRepository customerRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;
    private final InvoiceNumberGenerator invoiceNumberGenerator;

    @Transactional
    public PaddySaleDto.Response createSale(PaddySaleDto.CreateRequest request) {
        log.info("Creating paddy sale for batch: {}", request.getBatchNumber());

        // 1. Validate sale data
        validateSaleData(request);

        // 2. Get or create customer
        CustomerEntity customer = getOrCreateCustomer(request);

        // 3. Lock and validate stock
        PaddyStock stock = paddyStockRepository.findById(request.getPaddyStockId())
                .orElseThrow(() -> new StockNotFoundException("Paddy stock not found with ID: " + request.getPaddyStockId()));

        // Check sufficient stock
        if (stock.getQuantity().compareTo(request.getQuantity()) < 0) {
            throw new InsufficientStockException(
                String.format("Insufficient stock. Available: %s KG, Requested: %s KG",
                    stock.getQuantity(), request.getQuantity())
            );
        }

        // 4. Validate credit limit for CREDIT payment
        if (request.getPaymentMethod() == PaymentMethod.CREDIT) {
            validateCreditLimit(customer, request);
        }

        // 5. Calculate amounts
        BigDecimal subtotal = CalculationUtil.calculateSubtotal(request.getQuantity(), request.getPricePerKg());
        BigDecimal discountAmount = CalculationUtil.calculateDiscountAmount(subtotal, request.getDiscount());
        BigDecimal totalAmount = CalculationUtil.calculatePaddyTotal(subtotal, discountAmount, request.getDeliveryCharge());
        BigDecimal balanceAmount = CalculationUtil.calculateBalance(totalAmount, request.getPaidAmount());

        // 6. Generate unique invoice number
        String invoiceNumber = generateUniqueInvoiceNumber();

        // 7. Create sale entity
        PaddySale sale = PaddySale.builder()
                .invoiceNumber(invoiceNumber)
                .paddyStock(stock)
                .batchNumber(request.getBatchNumber())
                .quantity(request.getQuantity())
                .pricePerKg(request.getPricePerKg())
                .subtotal(subtotal)
                .discount(request.getDiscount())
                .discountAmount(discountAmount)
                .deliveryCharge(request.getDeliveryCharge())
                .totalAmount(totalAmount)
                .paidAmount(request.getPaidAmount())
                .balanceAmount(balanceAmount)
                .customer(customer)
                .saleDate(request.getSaleDate())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .deliveryRequired(request.getDeliveryRequired())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryDate(request.getDeliveryDate())
                .notes(request.getNotes())
                .build();

        // 8. Update stock quantity
        stock.setQuantity(stock.getQuantity().subtract(request.getQuantity()));
        paddyStockRepository.save(stock);

        // 9. Save sale
        sale = paddySaleRepository.save(sale);

        // 10. Update customer balance if credit
        if (request.getPaymentMethod() == PaymentMethod.CREDIT) {
            customer.setOutstandingBalance(customer.getOutstandingBalance().add(balanceAmount));
            customerRepository.save(customer);
        }

        // 11. Create payment transaction
        createPaymentTransaction(sale);

        log.info("Paddy sale created successfully: {}", invoiceNumber);

        return toResponse(sale, stock.getQuantity());
    }

    @Transactional(readOnly = true)
    public PaddySaleDto.Response getSaleById(UUID id) {
        log.info("Fetching paddy sale by ID: {}", id);
        PaddySale sale = paddySaleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Paddy sale not found with ID: " + id));
        return toResponse(sale, sale.getPaddyStock().getQuantity());
    }

    @Transactional(readOnly = true)
    public Page<PaddySaleDto.Response> getSalesHistory(UUID customerId, PaymentStatus paymentStatus,
                                                        LocalDate startDate, LocalDate endDate,
                                                        Pageable pageable) {
        log.info("Fetching paddy sales history");
        Page<PaddySale> sales = paddySaleRepository.findByFilters(customerId, paymentStatus, startDate, endDate, pageable);
        return sales.map(sale -> toResponse(sale, sale.getPaddyStock().getQuantity()));
    }

    @Transactional
    public PaddySaleDto.Response updatePayment(UUID saleId, PaddySaleDto.UpdatePaymentRequest request) {
        log.info("Updating payment for sale: {}", saleId);

        PaddySale sale = paddySaleRepository.findById(saleId)
                .orElseThrow(() -> new SaleNotFoundException("Paddy sale not found with ID: " + saleId));

        // Validate payment amount
        BigDecimal newTotalPaid = sale.getPaidAmount().add(request.getPaidAmount());
        if (newTotalPaid.compareTo(sale.getTotalAmount()) > 0) {
            throw new InvalidPaymentException("Payment amount exceeds total amount");
        }

        // Update sale
        sale.setPaidAmount(newTotalPaid);
        sale.setBalanceAmount(sale.getTotalAmount().subtract(newTotalPaid));
        sale.setPaymentStatus(request.getPaymentStatus());

        // Update customer balance
        if (sale.getPaymentMethod() == PaymentMethod.CREDIT) {
            CustomerEntity customer = sale.getCustomer();
            customer.setOutstandingBalance(customer.getOutstandingBalance().subtract(request.getPaidAmount()));
            customerRepository.save(customer);
        }

        sale = paddySaleRepository.save(sale);

        // Create payment transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .saleId(sale.getId())
                .saleType(SaleType.PADDY)
                .amount(request.getPaidAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentDate(LocalDateTime.now())
                .referenceNumber(request.getReferenceNumber())
                .notes(request.getNotes())
                .build();
        paymentTransactionRepository.save(transaction);

        log.info("Payment updated successfully for sale: {}", saleId);
        return toResponse(sale, sale.getPaddyStock().getQuantity());
    }

    private void validateSaleData(PaddySaleDto.CreateRequest request) {
        if (request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSaleDataException("Quantity must be greater than 0");
        }

        if (request.getPricePerKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSaleDataException("Price per kg must be greater than 0");
        }

        if (request.getSaleDate().isAfter(LocalDate.now())) {
            throw new InvalidSaleDataException("Sale date cannot be in the future");
        }

        if (request.getPaidAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSaleDataException("Paid amount cannot be negative");
        }

        if (request.getDeliveryRequired() && (request.getDeliveryAddress() == null || request.getDeliveryAddress().trim().isEmpty())) {
            throw new InvalidSaleDataException("Delivery address is required when delivery is needed");
        }
    }

    private CustomerEntity getOrCreateCustomer(PaddySaleDto.CreateRequest request) {
        return customerRepository.findByContact(request.getCustomerContact())
                .orElseGet(() -> {
                    CustomerEntity newCustomer = CustomerEntity.builder()
                            .name(request.getCustomerName())
                            .contact(request.getCustomerContact())
                            .email(request.getCustomerEmail())
                            .address(request.getCustomerAddress())
                            .customerType(CustomerType.RETAIL)
                            .creditLimit(BigDecimal.ZERO)
                            .outstandingBalance(BigDecimal.ZERO)
                            .build();
                    return customerRepository.save(newCustomer);
                });
    }

    private void validateCreditLimit(CustomerEntity customer, PaddySaleDto.CreateRequest request) {
        BigDecimal subtotal = CalculationUtil.calculateSubtotal(request.getQuantity(), request.getPricePerKg());
        BigDecimal discountAmount = CalculationUtil.calculateDiscountAmount(subtotal, request.getDiscount());
        BigDecimal totalAmount = CalculationUtil.calculatePaddyTotal(subtotal, discountAmount, request.getDeliveryCharge());
        BigDecimal newBalance = totalAmount.subtract(request.getPaidAmount());

        BigDecimal totalOutstanding = customer.getOutstandingBalance().add(newBalance);

        if (totalOutstanding.compareTo(customer.getCreditLimit()) > 0) {
            throw new CustomerCreditLimitExceededException(
                String.format("Customer credit limit exceeded. Limit: %s, Current Outstanding: %s, New Order: %s",
                    customer.getCreditLimit(), customer.getOutstandingBalance(), newBalance)
            );
        }
    }

    private String generateUniqueInvoiceNumber() {
        String invoiceNumber;
        int attempts = 0;
        do {
            invoiceNumber = invoiceNumberGenerator.generatePaddyInvoiceNumber();
            attempts++;
            if (attempts > 100) {
                throw new InvalidSaleDataException("Unable to generate unique invoice number after 100 attempts");
            }
        } while (paddySaleRepository.existsByInvoiceNumber(invoiceNumber));

        return invoiceNumber;
    }

    private void createPaymentTransaction(PaddySale sale) {
        if (sale.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .saleId(sale.getId())
                    .saleType(SaleType.PADDY)
                    .amount(sale.getPaidAmount())
                    .paymentMethod(sale.getPaymentMethod())
                    .paymentDate(LocalDateTime.now())
                    .notes("Initial payment for sale")
                    .build();
            paymentTransactionRepository.save(transaction);
        }
    }

    private PaddySaleDto.Response toResponse(PaddySale sale, BigDecimal remainingStock) {
        String createdByName = null;
        if (sale.getCreatedBy() != null) {
            createdByName = userRepository.findById(sale.getCreatedBy())
                    .map(User::getUsername)
                    .orElse("Unknown");
        }

        return PaddySaleDto.Response.builder()
                .saleId(sale.getId())
                .invoiceNumber(sale.getInvoiceNumber())
                .batchNumber(sale.getBatchNumber())
                .quantity(sale.getQuantity())
                .pricePerKg(sale.getPricePerKg())
                .subtotal(sale.getSubtotal())
                .discountAmount(sale.getDiscountAmount())
                .deliveryCharge(sale.getDeliveryCharge())
                .totalAmount(sale.getTotalAmount())
                .paidAmount(sale.getPaidAmount())
                .balanceAmount(sale.getBalanceAmount())
                .paymentStatus(sale.getPaymentStatus())
                .customerName(sale.getCustomer().getName())
                .saleDate(sale.getSaleDate().atStartOfDay())
                .invoiceUrl(sale.getInvoiceUrl())
                .remainingStock(remainingStock)
                .createdBy(createdByName)
                .build();
    }
}

