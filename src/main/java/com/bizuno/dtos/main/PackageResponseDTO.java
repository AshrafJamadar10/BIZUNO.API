package com.bizuno.dtos.main;

import com.bizuno.enums.ModelEnums;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PackageResponseDTO {
    private UUID packageId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private ModelEnums.PackageBillingPeriod billingPeriod;
    private Integer packageDays;
    private Integer trialDays;
    private BigDecimal setupFee;
    private Integer displayOrder;
    private Boolean recommended;
    private Set<PackageFeatureResponseDTO> features;
}
