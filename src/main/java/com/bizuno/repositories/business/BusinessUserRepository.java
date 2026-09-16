package com.bizuno.repositories.business;

import com.bizuno.models.business.BusinessUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BusinessUserRepository extends JpaRepository<BusinessUser, UUID> {
    @Query("SELECT bu FROM BusinessUser bu WHERE bu.phoneNumber = :phoneOrEmail OR bu.email = :phoneOrEmail")
    Optional<BusinessUser> findByCredential(@Param("phoneOrEmail") String phoneOrEmail);
}
