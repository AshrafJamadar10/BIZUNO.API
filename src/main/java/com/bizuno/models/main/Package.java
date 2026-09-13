package com.bizuno.models.main;

import com.bizuno.enums.ModelEnums;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "packages", indexes = {
        @Index(name = "idx_package_name", columnList = "name"),
        @Index(name = "idx_package_billing_period", columnList = "billingPeriod")
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Package extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID packageId;

    @NotBlank(message = "Package name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 500)
    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base price must be greater than or equal to 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @NotNull(message = "Billing period is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModelEnums.PackageBillingPeriod billingPeriod;

    @NotNull(message = "Package days is required")
    @Min(value = 0, message = "Package days must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer packageDays;

    @NotNull(message = "Trial days is required")
    @Min(value = 0, message = "Trial days must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer trialDays;

    @Builder.Default
    @DecimalMin(value = "0.0", inclusive = true, message = "Setup fee must be greater than or equal to 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal setupFee = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean recommended = false;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PackageFeature> features = new HashSet<>();

    @PrePersist
    @PreUpdate
    public void normalizePackageData() {
        if (this.name != null) {
            this.name = this.name.trim();
        }
        if (this.description != null) {
            this.description = this.description.trim();
        }
        if (this.basePrice == null) {
            this.basePrice = BigDecimal.ZERO;
        }
        if (this.setupFee == null) {
            this.setupFee = BigDecimal.ZERO;
        }
        if (this.packageDays == null) {
            this.packageDays = 0;
        }
        if (this.trialDays == null) {
            this.trialDays = 0;
        }
        if (this.displayOrder == null) {
            this.displayOrder = 0;
        }
        if (this.recommended == null) {
            this.recommended = false;
        }
        if (this.features != null) {
            this.features.forEach(feature -> {
                if (feature.getPack() == null) {
                    feature.setPack(this);
                }
            });
        }
    }

    public void addFeature(String featureCode, String featureName, String description,
                          ModelEnums.CrudPermissionScopes scope, Set<String> operations,
                          ModelEnums.FeatureLimitType limitType, Integer limitValue,
                          String unit, Boolean isEnabled, int displayOrder) {
        PackageFeature feature = PackageFeature.builder()
                .packageFeatureCode(featureCode)
                .featureName(featureName)
                .description(description)
                .scope(scope.name())
                .displayOrder(displayOrder)
                .limitType(limitType != null ? limitType : ModelEnums.FeatureLimitType.NONE)
                .limitValue(limitValue != null ? limitValue : 0)
                .unit(unit)
                .isEnabled(isEnabled != null ? isEnabled : true)
                .pack(this)
                .build();

        if (operations != null) {
            operations.forEach(feature::addOperation);
        }

        this.features.add(feature);
    }

    public void removeFeature(PackageFeature feature) {
        if (feature == null) {
            return;
        }
        this.features.remove(feature);
        feature.setPack(null);
    }

    public void clearFeatures() {
        if (this.features == null) {
            return;
        }
        this.features.forEach(feature -> feature.setPack(null));
        this.features.clear();
    }

    public Set<String> getScopes() {
        if (this.features == null || this.features.isEmpty()) {
            Set<String> defaultScopes = new HashSet<>();
            defaultScopes.add("DASHBOARD");
            defaultScopes.add("CUSTOMERS");
            defaultScopes.add("INVENTORY");
            defaultScopes.add("EMPLOYEES");
            return defaultScopes;
        }

        Set<String> derivedScopes = new HashSet<>();
        for (PackageFeature feature : this.features) {
            if (feature == null || feature.getScope() == null) {
                continue;
            }
            derivedScopes.add(feature.getScope());
        }

        return derivedScopes.isEmpty() ? Set.of() : derivedScopes;
    }

    public Set<String> getOperations() {
        if (this.features == null || this.features.isEmpty()) {
            return Set.of("CREATE", "READ", "UPDATE", "DELETE");
        }

        Set<String> derivedOperations = new HashSet<>();
        for (PackageFeature feature : this.features) {
            if (feature == null || feature.getOperations() == null) {
                continue;
            }
            derivedOperations.addAll(feature.getOperations());
        }

        return derivedOperations.isEmpty() ? Set.of() : derivedOperations;
    }
}
