package com.bizuno.models.business;

import com.bizuno.models.main.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "purchase_order_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    @JsonIgnore
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Unit cost must be greater than or equal to 0")
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0", inclusive = true, message = "Line total must be greater than or equal to 0")
    private BigDecimal lineTotal = BigDecimal.ZERO;
}
