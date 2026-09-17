package com.bizuno.repositories.business;

import com.bizuno.models.business.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean existsByPaymentReferenceAndPaymentIdNot(String paymentReference, UUID paymentId);
    boolean existsByPaymentReference(String paymentReference);
}
