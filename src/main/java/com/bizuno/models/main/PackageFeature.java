package com.bizuno.models.main;

import com.bizuno.enums.ModelEnums;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "package_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID packageFeatureId;

    @NotBlank(message = "Feature code is required")
    @Column(nullable = false)
    private String packageFeatureCode;

    @NotBlank(message = "Feature name is required")
    @Column(nullable = false)
    private String featureName;

    private String description;

    @NotNull(message = "Scope is required")
    @Column(nullable = false)
    private String scope;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "package_feature_operations",
            joinColumns = @JoinColumn(name = "package_feature_id")
    )
    @Column(name = "operation", nullable = false)
    @Builder.Default
    private Set<String> operations = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ModelEnums.FeatureLimitType limitType = ModelEnums.FeatureLimitType.NONE;

    @Min(value = 0, message = "Limit value must be greater than or equal to 0")
    @Builder.Default
    private Integer limitValue = 0;

    private String unit;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isEnabled = true;

    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private Package pack;

    public void addOperation(String operation) {
        if (operation != null && !operation.trim().isEmpty()) {
            this.operations.add(operation.trim().toUpperCase());
        }
    }

    public void removeOperation(String operation) {
        if (operation != null) {
            this.operations.remove(operation.trim().toUpperCase());
        }
    }
}

