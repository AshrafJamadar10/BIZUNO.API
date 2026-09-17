package com.bizuno.dtos.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class UpdatePurchaseOrderRequestDTO {
    private UUID supplierId;

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    private String orderNumber;

    @NotNull(message = "Order date is required")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime orderDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expectedDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Subtotal must be greater than or equal to 0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be greater than or equal to 0")
    private BigDecimal taxAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be greater than or equal to 0")
    private BigDecimal total;

    @NotNull(message = "Status is required")
    private String status;
}
