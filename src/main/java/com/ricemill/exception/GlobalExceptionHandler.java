package com.ricemill.exception;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.error("Business exception: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("BUSINESS_ERROR")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleStockNotFound(StockNotFoundException ex) {
        log.error("Stock not found: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("STOCK_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(error));
    }

    @ExceptionHandler(DuplicateBatchNumberException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateBatchNumber(DuplicateBatchNumberException ex) {
        log.error("Duplicate batch number: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("DUPLICATE_BATCH_NUMBER")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(error));
    }

    @ExceptionHandler(InvalidStockDataException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidStockData(InvalidStockDataException ex) {
        log.error("Invalid stock data: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("INVALID_STOCK_DATA")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientStock(InsufficientStockException ex) {
        log.error("Insufficient stock: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("INSUFFICIENT_STOCK")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    @ExceptionHandler(InvalidSaleDataException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSaleData(InvalidSaleDataException ex) {
        log.error("Invalid sale data: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("INVALID_SALE_DATA")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    @ExceptionHandler(CustomerCreditLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleCreditLimitExceeded(CustomerCreditLimitExceededException ex) {
        log.error("Credit limit exceeded: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("CREDIT_LIMIT_EXCEEDED")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    @ExceptionHandler(SaleNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleSaleNotFound(SaleNotFoundException ex) {
        log.error("Sale not found: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("SALE_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(error));
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPayment(InvalidPaymentException ex) {
        log.error("Invalid payment: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("INVALID_PAYMENT")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomerNotFound(CustomerNotFoundException ex) {
        log.error("Customer not found: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("CUSTOMER_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(error));
    }

    @ExceptionHandler(InsufficientPaddyStockException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientPaddyStock(InsufficientPaddyStockException ex) {
        log.error("Insufficient paddy stock: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("INSUFFICIENT_PADDY_STOCK")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    @ExceptionHandler(InvalidThreshingDataException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidThreshingData(InvalidThreshingDataException ex) {
        log.error("Invalid threshing data: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("INVALID_THRESHING_DATA")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    @ExceptionHandler(ThreshingRecordNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleThreshingRecordNotFound(ThreshingRecordNotFoundException ex) {
        log.error("Threshing record not found: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("THRESHING_RECORD_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        ErrorResponse error = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("INVALID_CREDENTIALS")
                .message("Invalid username or password")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .code("ACCESS_DENIED")
                .message("You don't have permission to access this resource")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(error));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        ErrorResponse error = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(error));
    }
}
