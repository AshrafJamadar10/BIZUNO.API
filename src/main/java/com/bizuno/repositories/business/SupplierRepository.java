package com.bizuno.repositories.business;

import com.bizuno.models.business.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    boolean existsByNameAndSupplierIdNot(String name, UUID supplierId);
    boolean existsByName(String name);
    boolean existsByEmailAndSupplierIdNot(String email, UUID supplierId);
    boolean existsByEmail(String email);
    boolean existsByPhoneAndSupplierIdNot(String phone, UUID supplierId);
    boolean existsByPhone(String phone);
}
