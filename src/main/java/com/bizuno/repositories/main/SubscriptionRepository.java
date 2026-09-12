package com.bizuno.repositories.main;

import com.bizuno.models.main.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    @Query("SELECT s FROM Subscription s WHERE s.business.businessCode = :businessCode " +
            "AND s.subscriptionStatus IN ('ACTIVE', 'TRIALING') " +
            "AND s.startDate <= CURRENT_TIMESTAMP " +
            "AND s.endDate >= CURRENT_TIMESTAMP " +
            "ORDER BY s.createdDate DESC")
    Optional<Subscription> findCurrentSubscriptionByBusinessCode(@Param("businessCode") String businessCode);
}
