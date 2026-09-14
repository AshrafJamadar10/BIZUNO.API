package com.bizuno.dtos.main;

import com.bizuno.enums.ModelEnums;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PackageFeatureRequestDTO {

    @NotBlank(message = "Feature code is required")
    private String featureCode;

    @NotBlank(message = "Feature name is required")
    private String featureName;

    private String description;

    @NotNull(message = "Scope is required")
    private ModelEnums.CrudPermissionScopes scope;

    private Set<String> operations = new HashSet<>();

    private ModelEnums.FeatureLimitType limitType = ModelEnums.FeatureLimitType.NONE;

    @Min(value = 0, message = "Limit value must be greater than or equal to 0")
    private Integer limitValue = 0;

    private String unit;
    private Boolean isEnabled = true;
    private Integer displayOrder = 0;
}
