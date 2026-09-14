package com.bizuno.models.main;

import com.bizuno.enums.ModelEnums;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID subscriptionId;

    @Builder.Default
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.SubscriptionStatus subscriptionStatus = ModelEnums.SubscriptionStatus.ACTIVE;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime endDate;

    private LocalDateTime trialEndDate;
    private LocalDateTime canceledAt;
    private LocalDateTime gracePeriodEndsAt;

    // Payment Information

    @Builder.Default
    private Boolean isPaid = false;

    private String transactionId;
    private String invoiceNumber;
    private BigDecimal amountPaid;
    private String paidThrough;
    private LocalDateTime paidAt;

    // Renewal & Upgrade Info
    @Builder.Default
    private Boolean autoRenew = true;

    private String cancellationReason;

    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id", nullable = false)
    @JsonIgnore
    private Package pack;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id", nullable = false)
    @JsonIgnore
    private Business business;

    @PrePersist
    protected void onCreate() {
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        calculateEndDate();
    }

    // Business logic methods

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return subscriptionStatus == ModelEnums.SubscriptionStatus.ACTIVE ||
                subscriptionStatus == ModelEnums.SubscriptionStatus.TRIALING &&
                        startDate.isBefore(now) && endDate.isAfter(now);
    }

    public boolean isInTrial() {
        return trialEndDate != null && LocalDateTime.now().isBefore(trialEndDate);
    }

    public boolean canCancel() {
        return subscriptionStatus == ModelEnums.SubscriptionStatus.ACTIVE ||
                subscriptionStatus == ModelEnums.SubscriptionStatus.TRIALING;
    }

    public long getDaysRemaining() {
        return ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
    }

    // Automatically calculate end date based on subscription
    private void calculateEndDate() {
        if (pack != null && startDate != null) {
            switch (pack.getBillingPeriod()) {
                case MONTHLY:
                    this.endDate = startDate.plusMonths(1);
                    break;
                case QUARTERLY:
                    this.endDate = startDate.plusMonths(3);
                    break;
                case YEARLY:
                    this.endDate = startDate.plusYears(1);
                    break;
                case LIFETIME:
                    this.endDate = startDate.plusYears(100);
                    break;
                default:
                    this.endDate = startDate.plusMonths(1);
            }

            // Set trial end date if subscription has trial
            if (pack.getTrialDays() != null && pack.getTrialDays() > 0) {
                this.trialEndDate = startDate.plusDays(pack.getTrialDays());
            }
        }
    }

    // Business operations

    public void cancel() {
        if (canCancel()) {
            this.subscriptionStatus = ModelEnums.SubscriptionStatus.CANCELED;
            this.canceledAt = LocalDateTime.now();
            this.autoRenew = false;
        }
    }

    public void activate() {
        this.subscriptionStatus = ModelEnums.SubscriptionStatus.ACTIVE;
        this.isPaid = true;
        this.paidAt = LocalDateTime.now();
    }

    public void markPastDue() {
        this.subscriptionStatus = ModelEnums.SubscriptionStatus.PAST_DUE;
        this.gracePeriodEndsAt = LocalDateTime.now().plusDays(7);
    }
}
