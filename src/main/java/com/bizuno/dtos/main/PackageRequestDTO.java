package com.bizuno.dtos.main;

import com.bizuno.enums.ModelEnums;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PackageRequestDTO {

    @NotBlank(message = "Package name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base price must be greater than or equal to 0")
    private BigDecimal basePrice;

    @NotNull(message = "Billing period is required")
    private ModelEnums.PackageBillingPeriod billingPeriod;

    @NotNull(message = "Package days is required")
    @Min(value = 0, message = "Package days must be greater than or equal to 0")
    private Integer packageDays;

    @NotNull(message = "Trial days is required")
    @Min(value = 0, message = "Trial days must be greater than or equal to 0")
    private Integer trialDays;

    @DecimalMin(value = "0.0", inclusive = true, message = "Setup fee must be greater than or equal to 0")
    private BigDecimal setupFee = BigDecimal.ZERO;

    private Integer displayOrder = 0;
    private Boolean recommended = false;

    private List<PackageFeatureRequestDTO> features = new ArrayList<>();
}
