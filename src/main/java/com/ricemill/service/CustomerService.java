package com.ricemill.service;

import com.ricemill.dto.CustomerDto;
import com.ricemill.entity.CustomerEntity;
import com.ricemill.exception.ResourceNotFoundException;
import com.ricemill.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public Page<CustomerDto.Response> getAllCustomers(Pageable pageable) {
        return customerRepository.findByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }
    
    public CustomerDto.Response getCustomerById(UUID id) {
        CustomerEntity customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return toResponse(customer);
    }
    
    @Transactional
    public CustomerDto.Response createCustomer(CustomerDto.CreateRequest request) {
        CustomerEntity customer = CustomerEntity.builder()
                .name(request.getName())
                .contact(request.getPhone())
                .address(request.getAddress())
                .build();
        
        customer = customerRepository.save(customer);
        return toResponse(customer);
    }
    
    @Transactional
    public CustomerDto.Response updateCustomer(UUID id, CustomerDto.UpdateRequest request) {
        CustomerEntity customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (request.getName() != null) {
            customer.setName(request.getName());
        }
        if (request.getPhone() != null) {
            customer.setContact(request.getPhone());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }
        if (request.getActive() != null) {
            customer.setActive(request.getActive());
        }
        
        customer = customerRepository.save(customer);
        return toResponse(customer);
    }
    
    @Transactional
    public void deleteCustomer(UUID id) {
        CustomerEntity customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setDeletedAt(LocalDateTime.now());
        customer.setActive(false);
        customerRepository.save(customer);
    }
    
    private CustomerDto.Response toResponse(CustomerEntity customer) {
        return CustomerDto.Response.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getContact())
                .address(customer.getAddress())
                .active(customer.getActive())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
