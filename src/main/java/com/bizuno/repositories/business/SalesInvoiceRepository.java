package com.bizuno.repositories.business;

import com.bizuno.models.business.SalesInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, UUID> {
    boolean existsByInvoiceNumberAndSalesInvoiceIdNot(String invoiceNumber, UUID salesInvoiceId);
    boolean existsByInvoiceNumber(String invoiceNumber);
}
