package com.bizuno.dtos.business;

import com.bizuno.enums.ModelEnums;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class CreateSalesInvoiceRequestDTO {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceNumber;

    @NotNull(message = "Issued date is required")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime issuedAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dueDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Subtotal must be greater than or equal to 0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be greater than or equal to 0")
    private BigDecimal taxAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be greater than or equal to 0")
    private BigDecimal total;

    @DecimalMin(value = "0.0", inclusive = true, message = "Amount paid must be greater than or equal to 0")
    private BigDecimal amountPaid;

    @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be greater than or equal to 0")
    private BigDecimal balance;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.InvoiceStatus status = ModelEnums.InvoiceStatus.DRAFT;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.PaymentStatus paymentStatus = ModelEnums.PaymentStatus.PENDING;

    @Size(max = 100, message = "Salesperson must not exceed 100 characters")
    private String salesperson;

    private List<CreateSalesInvoiceItemRequestDTO> items;
}
