package com.bizuno.dtos.business;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class CreateSalesInvoiceItemRequestDTO {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be greater than or equal to 0")
    private BigDecimal quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be greater than or equal to 0")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be greater than or equal to 0")
    private BigDecimal discount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax must be greater than or equal to 0")
    private BigDecimal tax;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be greater than or equal to 0")
    private BigDecimal total;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
