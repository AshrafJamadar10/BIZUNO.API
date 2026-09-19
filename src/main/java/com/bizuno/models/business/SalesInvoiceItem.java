package com.bizuno.models.business;

import com.bizuno.models.main.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sales_invoice_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesInvoiceItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID salesInvoiceItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnore
    private SalesInvoice salesInvoice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be greater than or equal to 0")
    private BigDecimal unitPrice;

    private BigDecimal discountPercent = BigDecimal.ZERO;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be greater than or equal to 0")
    private BigDecimal taxRate = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Line total must be greater than or equal to 0")
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
}
