package com.bizuno.repositories.business;

import com.bizuno.models.business.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByNameAndCustomerIdNot(String name, UUID customerId);
    boolean existsByName(String name);
    boolean existsByEmailAndCustomerIdNot(String email, UUID customerId);
    boolean existsByEmail(String email);
    boolean existsByPhoneAndCustomerIdNot(String phone, UUID customerId);
    boolean existsByPhone(String phone);
}
