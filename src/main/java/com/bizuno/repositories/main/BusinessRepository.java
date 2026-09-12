package com.bizuno.repositories.main;

import com.bizuno.models.main.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BusinessRepository extends JpaRepository<Business, UUID> {
    Optional<Business> findByTenantId(String id);

    boolean existsByBusinessCode(String businessCode);

    boolean existsByTenantId(String tenantId);

    Optional<Business> findByBusinessCode(String businessCode);
}
