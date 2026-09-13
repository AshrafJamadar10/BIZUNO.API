package com.bizuno.dtos.main;

import com.bizuno.enums.ModelEnums;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder
public class PackageFeatureResponseDTO {
    private UUID packageFeatureId;
    private String packageFeatureCode;
    private String featureName;
    private String description;
    private String scope;
    private Set<String> operations;
    private ModelEnums.FeatureLimitType limitType;
    private Integer limitValue;
    private String unit;
    private Boolean isEnabled;
    private Integer displayOrder;
}
