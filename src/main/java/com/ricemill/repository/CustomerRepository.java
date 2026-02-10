package com.ricemill.repository;

import com.ricemill.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByContact(String contact);

    Optional<CustomerEntity> findByEmail(String email);

    boolean existsByContact(String contact);

    Page<CustomerEntity> findByDeletedAtIsNull(Pageable pageable);

    Optional<CustomerEntity> findByIdAndDeletedAtIsNull(UUID id);
}

