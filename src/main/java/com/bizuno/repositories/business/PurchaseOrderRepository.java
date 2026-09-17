package com.bizuno.repositories.business;

import com.bizuno.models.business.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    boolean existsByOrderNumberAndPurchaseOrderIdNot(String orderNumber, UUID purchaseOrderId);
    boolean existsByOrderNumber(String orderNumber);
}
