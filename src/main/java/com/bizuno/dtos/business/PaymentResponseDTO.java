package com.bizuno.dtos.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private UUID paymentId;
    private UUID customerId;
    private String customerName;
    private UUID salesInvoiceId;
    private String invoiceNumber;
    private String paymentReference;
    private String paymentMethod;
    private BigDecimal amount;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime receivedAt;
    private String status;
    private String paymentMethodDetails;
    private String note;
}
