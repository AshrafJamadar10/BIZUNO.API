package com.bizuno.models.main;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "package_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageFeatureId;

    @NotBlank(message = "Feature code is required")
    private String packageFeatureCode;

    @NotBlank(message = "Feature name is required")
    private String featureName;

    private String description;

    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private Package pack;
}

