package com.bizuno.dtos.main;

import com.bizuno.enums.ModelEnums;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SubscriptionResponseDTO {
    private UUID subscriptionId;
    private ModelEnums.SubscriptionStatus subscriptionStatus;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime endDate;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime trialEndDate;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime canceledAt;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime gracePeriodEndsAt;
    
    private Boolean isPaid;
    private String transactionId;
    private String invoiceNumber;
    private BigDecimal amountPaid;
    private String paidThrough;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime paidAt;
    
    private Boolean autoRenew;
    private String cancellationReason;
    
    private UUID packageId;
    private String packageName;
    private UUID businessId;
    private String businessName;
    private String businessCode;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedDate;
    
    private Boolean isActive;
}
