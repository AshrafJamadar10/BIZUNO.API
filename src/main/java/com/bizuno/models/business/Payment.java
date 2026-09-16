package com.bizuno.models.business;

import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.BaseEntity;
import com.bizuno.models.main.Business;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    @JsonIgnore
    private SalesInvoice salesInvoice;

    @NotBlank(message = "Payment reference is required")
    @Size(max = 100, message = "Payment reference must not exceed 100 characters")
    @Column(unique = true)
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
