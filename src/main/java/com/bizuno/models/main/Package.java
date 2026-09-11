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
@Table(name = "packages")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Package extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID packageId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    @Column(length = 500)
    private String description;

    @NotNull(message = "Base price is required")
    @Min(value = 0, message = "Base price must be greater than or equal to 0")
    private BigDecimal basePrice;

    @NotNull(message = "Billing period is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.PackageBillingPeriod billingPeriod;

    @NotNull(message = "Package days is required")
    @Min(value = 0, message = "Package days must be greater than or equal to 0")
    private Integer packageDays;

    @NotNull(message = "Trial days is required")
    @Min(value = 0, message = "Trial days must be greater than or equal to 0")
    private Integer trialDays;

    private BigDecimal setupFee;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean recommended = false;

//    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Builder.Default
//    private Set<PackageFeature> features = new HashSet<>();

//    @Builder.Default
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable( name = "package_scopes", joinColumns = @JoinColumn(name = "package_id"))
//    @Column(name = "scope", nullable = false)
//    private Set<String> scopes = new HashSet<>();
//
//    @Builder.Default
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable( name = "operations", joinColumns = @JoinColumn(name = "package_id"))
//    @Column(name = "operation", nullable = false)
//    private Set<String> operations = new HashSet<>();

    // Helper methods
//    public void addFeature(String featureCode, String featureName, String description, int displayOrder) {
//        PackageFeature feature = PackageFeature.builder()
//                .packageFeatureCode(featureCode)
//                .featureName(featureName)
//                .description(description)
//                .displayOrder(displayOrder)
//                .pack(this)
//                .build();
//        features.add(feature);
//    }
//
//    public void removeFeature(PackageFeature feature) {
//        features.remove(feature);
//        feature.setPack(null);
//    }

//    public void addScope(String scope){
//        scopes.add(scope);
//    }
//
//    public void removeScope(String scope){
//        scopes.remove(scope);
//    }
//
//    public void addOperation(String operation){
//        operations.add(operation);
//    }
//
//    public void removeOperation(String operation){
//        operations.remove(operation);
//    }
}
