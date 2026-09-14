package com.bizuno.dtos.main;

import com.bizuno.enums.ModelEnums;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateSubscriptionRequestDTO {
    @NotNull(message = "Package ID is required")
    private UUID packageId;

    @NotNull(message = "Business ID is required")
    private UUID businessId;

    @NotNull(message = "Subscription Status is required")
    private ModelEnums.SubscriptionStatus subscriptionStatus;

    @NotNull(message = "Start Date is required")
    private LocalDateTime startDate;

    private Boolean autoRenew = true;

    private String transactionId;

    private String invoiceNumber;

    private BigDecimal amountPaid;

    private String paidThrough;

}
