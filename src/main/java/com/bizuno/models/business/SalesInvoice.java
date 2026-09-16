package com.bizuno.models.business;

import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.BaseEntity;
import com.bizuno.models.main.Business;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales_invoice")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesInvoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID salesInvoiceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    @Column(unique = true)
    private String invoiceNumber;

    @NotNull(message = "Issued date is required")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime issuedAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dueDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Subtotal must be greater than or equal to 0")
    private BigDecimal subtotal = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be greater than or equal to 0")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be greater than or equal to 0")
    private BigDecimal total = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Amount paid must be greater than or equal to 0")
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be greater than or equal to 0")
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.InvoiceStatus status = ModelEnums.InvoiceStatus.DRAFT;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.PaymentStatus paymentStatus = ModelEnums.PaymentStatus.PENDING;

    @Size(max = 100, message = "Salesperson must not exceed 100 characters")
    private String salesperson;

    @OneToMany(mappedBy = "salesInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SalesInvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "salesInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Payment> payments = new ArrayList<>();
}
