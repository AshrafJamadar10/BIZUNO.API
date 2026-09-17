package com.bizuno.dtos.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesInvoiceResponseDTO {
    private UUID salesInvoiceId;
    private UUID customerId;
    private String customerName;
    private String invoiceNumber;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime issuedAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dueDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private String status;
    private String paymentStatus;
    private String salesperson;
    private List<SalesInvoiceItemResponseDTO> items;
}
