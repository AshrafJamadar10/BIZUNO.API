package com.bizuno.models.business;

import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "custom_field")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomField extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID fieldId;

    @NotBlank(message = "Label is required")
    @Size(max = 100, message = "Label must not exceed 100 characters")
    @Column(name = "label")
    private String label;

    @NotBlank(message = "Field key is required")
    @Size(max = 50, message = "Field key must not exceed 50 characters")
    @Column(name = "field_key", unique = true)
    private String fieldKey;

    @Size(max = 255, message = "Placeholder must not exceed 255 characters")
    @Column(name = "placeholder")
    private String placeholder;

    @Size(max = 500, message = "Helper text must not exceed 500 characters")
    @Column(name = "helper_text")
    private String helperText;

    @Column(name = "required")
    private Boolean required = false;

    @Size(max = 1000, message = "Default value must not exceed 1000 characters")
    private String defaultValue;

    @NotNull(message = "Data type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type")
    private ModelEnums.CustomFieldDataType dataType;

    @Size(max = 1000, message = "Options must not exceed 1000 characters")
    @Column(name = "options")
    private String options;

    @Column(name = "text_color")
    private String textColor = "inherit";

    @Column(name = "field_background_color")
    private String fieldBackgroundColor = "inherit";

    @Column(name = "border_color")
    private String borderColor = "inherit";

    @Column(name = "border_radius")
    private Integer borderRadius = 0;

    @Column(name = "font_weight")
    private Integer fontWeight = 400;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ModelEnums.ProductStatus status = ModelEnums.ProductStatus.ACTIVE;

    @NotNull(message = "Custom field type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type")
    private ModelEnums.CustomFieldType fieldType = ModelEnums.CustomFieldType.INPUT;

    @Column(name = "formula", columnDefinition = "TEXT")
    private String formula;

    @ElementCollection
    @CollectionTable(name = "custom_field_dependencies",
            joinColumns = @JoinColumn(name = "field_id"))
    @Column(name = "dependency_field_key")
    private Set<String> dependentFieldKeys = new HashSet<>();

    @OneToMany(mappedBy = "customField", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomFieldValue> customFieldValues = new ArrayList<>();
}
