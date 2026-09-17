package com.bizuno.repositories.business;

import com.bizuno.models.business.SalesInvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalesInvoiceItemRepository extends JpaRepository<SalesInvoiceItem, UUID> {
}
