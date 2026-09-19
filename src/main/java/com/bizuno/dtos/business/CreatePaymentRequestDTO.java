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
import java.util.UUID;

@Setter
@Getter
public class CreatePaymentRequestDTO {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    private UUID salesInvoiceId;

    @NotBlank(message = "Payment reference is required")
    @Size(max = 100, message = "Payment reference must not exceed 100 characters")
    private String paymentReference;

    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.PaymentMethod paymentMethod;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be greater than or equal to 0")
    private BigDecimal amount;

    @NotNull(message = "Received date is required")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime receivedAt;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.PaymentStatus status = ModelEnums.PaymentStatus.PENDING;

    @NotNull(message = "Payment method details is required")
    @Size(max = 500, message = "Payment method details must not exceed 500 characters")
    private String paymentMethodDetails;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
}
