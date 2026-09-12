package com.bizuno.repositories.business;

import com.bizuno.models.business.BusinessUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessUserRepository extends JpaRepository<BusinessUser, UUID> {
}
