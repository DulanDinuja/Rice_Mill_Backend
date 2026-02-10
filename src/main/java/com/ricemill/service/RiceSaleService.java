package com.ricemill.service;

import com.ricemill.dto.RiceSaleDto;
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
public class RiceSaleService {

    private final RiceSaleRepository riceSaleRepository;
    private final RiceStockRepository riceStockRepository;
    private final CustomerRepository customerRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;
    private final InvoiceNumberGenerator invoiceNumberGenerator;

    @Transactional
    public RiceSaleDto.Response createSale(RiceSaleDto.CreateRequest request) {
        log.info("Creating rice sale for batch: {}", request.getBatchNumber());

        // 1. Validate sale data
        validateSaleData(request);

        // 2. Get or create customer
        CustomerEntity customer = getOrCreateCustomer(request);

        // 3. Validate stock
        RiceStock stock = riceStockRepository.findById(request.getRiceStockId())
                .orElseThrow(() -> new StockNotFoundException("Rice stock not found with ID: " + request.getRiceStockId()));

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
        BigDecimal taxAmount = CalculationUtil.calculateTaxAmount(subtotal.subtract(discountAmount), request.getTaxRate());
        BigDecimal totalAmount = CalculationUtil.calculateRiceTotal(subtotal, discountAmount, taxAmount, request.getDeliveryCharge());
        BigDecimal balanceAmount = CalculationUtil.calculateBalance(totalAmount, request.getPaidAmount());

        // 6. Generate unique invoice number
        String invoiceNumber = generateUniqueInvoiceNumber();

        // 7. Create sale entity
        RiceSale sale = RiceSale.builder()
                .invoiceNumber(invoiceNumber)
                .riceStock(stock)
                .batchNumber(request.getBatchNumber())
                .quantity(request.getQuantity())
                .packageType(request.getPackageType())
                .numberOfPackages(request.getNumberOfPackages())
                .pricePerKg(request.getPricePerKg())
                .pricePerPackage(request.getPricePerPackage())
                .subtotal(subtotal)
                .discount(request.getDiscount())
                .discountAmount(discountAmount)
                .taxRate(request.getTaxRate())
                .taxAmount(taxAmount)
                .deliveryCharge(request.getDeliveryCharge())
                .totalAmount(totalAmount)
                .paidAmount(request.getPaidAmount())
                .balanceAmount(balanceAmount)
                .customer(customer)
                .customerType(request.getCustomerType())
                .saleDate(request.getSaleDate())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .deliveryRequired(request.getDeliveryRequired())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryDate(request.getDeliveryDate())
                .vehicleNumber(request.getVehicleNumber())
                .notes(request.getNotes())
                .build();

        // 8. Update stock quantity
        stock.setQuantity(stock.getQuantity().subtract(request.getQuantity()));
        riceStockRepository.save(stock);

        // 9. Save sale
        sale = riceSaleRepository.save(sale);

        // 10. Update customer balance if credit
        if (request.getPaymentMethod() == PaymentMethod.CREDIT) {
            customer.setOutstandingBalance(customer.getOutstandingBalance().add(balanceAmount));
            customerRepository.save(customer);
        }

        // 11. Create payment transaction
        createPaymentTransaction(sale);

        log.info("Rice sale created successfully: {}", invoiceNumber);

        return toResponse(sale, stock.getQuantity());
    }

    @Transactional(readOnly = true)
    public RiceSaleDto.Response getSaleById(UUID id) {
        log.info("Fetching rice sale by ID: {}", id);
        RiceSale sale = riceSaleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Rice sale not found with ID: " + id));
        return toResponse(sale, sale.getRiceStock().getQuantity());
    }

    @Transactional(readOnly = true)
    public Page<RiceSaleDto.Response> getSalesHistory(UUID customerId, PaymentStatus paymentStatus,
                                                       LocalDate startDate, LocalDate endDate,
                                                       Pageable pageable) {
        log.info("Fetching rice sales history");
        Page<RiceSale> sales = riceSaleRepository.findByFilters(customerId, paymentStatus, startDate, endDate, pageable);
        return sales.map(sale -> toResponse(sale, sale.getRiceStock().getQuantity()));
    }

    private void validateSaleData(RiceSaleDto.CreateRequest request) {
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

    private CustomerEntity getOrCreateCustomer(RiceSaleDto.CreateRequest request) {
        return customerRepository.findByContact(request.getCustomerContact())
                .orElseGet(() -> {
                    CustomerEntity newCustomer = CustomerEntity.builder()
                            .name(request.getCustomerName())
                            .contact(request.getCustomerContact())
                            .email(request.getCustomerEmail())
                            .address(request.getCustomerAddress())
                            .customerType(request.getCustomerType())
                            .creditLimit(BigDecimal.ZERO)
                            .outstandingBalance(BigDecimal.ZERO)
                            .build();
                    return customerRepository.save(newCustomer);
                });
    }

    private void validateCreditLimit(CustomerEntity customer, RiceSaleDto.CreateRequest request) {
        BigDecimal subtotal = CalculationUtil.calculateSubtotal(request.getQuantity(), request.getPricePerKg());
        BigDecimal discountAmount = CalculationUtil.calculateDiscountAmount(subtotal, request.getDiscount());
        BigDecimal taxAmount = CalculationUtil.calculateTaxAmount(subtotal.subtract(discountAmount), request.getTaxRate());
        BigDecimal totalAmount = CalculationUtil.calculateRiceTotal(subtotal, discountAmount, taxAmount, request.getDeliveryCharge());
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
            invoiceNumber = invoiceNumberGenerator.generateRiceInvoiceNumber();
            attempts++;
            if (attempts > 100) {
                throw new InvalidSaleDataException("Unable to generate unique invoice number after 100 attempts");
            }
        } while (riceSaleRepository.existsByInvoiceNumber(invoiceNumber));

        return invoiceNumber;
    }

    private void createPaymentTransaction(RiceSale sale) {
        if (sale.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .saleId(sale.getId())
                    .saleType(SaleType.RICE)
                    .amount(sale.getPaidAmount())
                    .paymentMethod(sale.getPaymentMethod())
                    .paymentDate(LocalDateTime.now())
                    .notes("Initial payment for sale")
                    .build();
            paymentTransactionRepository.save(transaction);
        }
    }

    private RiceSaleDto.Response toResponse(RiceSale sale, BigDecimal remainingStock) {
        String createdByName = null;
        if (sale.getCreatedBy() != null) {
            createdByName = userRepository.findById(sale.getCreatedBy())
                    .map(User::getUsername)
                    .orElse("Unknown");
        }

        return RiceSaleDto.Response.builder()
                .saleId(sale.getId())
                .invoiceNumber(sale.getInvoiceNumber())
                .batchNumber(sale.getBatchNumber())
                .riceType(sale.getRiceStock().getRiceType())
                .quantity(sale.getQuantity())
                .numberOfPackages(sale.getNumberOfPackages())
                .pricePerKg(sale.getPricePerKg())
                .subtotal(sale.getSubtotal())
                .discountAmount(sale.getDiscountAmount())
                .taxAmount(sale.getTaxAmount())
                .deliveryCharge(sale.getDeliveryCharge())
                .totalAmount(sale.getTotalAmount())
                .paidAmount(sale.getPaidAmount())
                .balanceAmount(sale.getBalanceAmount())
                .paymentStatus(sale.getPaymentStatus())
                .customerName(sale.getCustomer().getName())
                .customerType(sale.getCustomerType())
                .saleDate(sale.getSaleDate().atStartOfDay())
                .invoiceUrl(sale.getInvoiceUrl())
                .remainingStock(remainingStock)
                .createdBy(createdByName)
                .build();
    }
}

