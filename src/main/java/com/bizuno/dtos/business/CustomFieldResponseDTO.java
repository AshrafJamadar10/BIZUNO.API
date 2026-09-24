package com.bizuno.dtos.business;

import com.bizuno.enums.ModelEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldResponseDTO {
    private UUID fieldId;
    private String label;
    private String fieldKey;
    private String placeholder;
    private String helperText;
    private Boolean required;
    private ModelEnums.CustomFieldDataType dataType;
    private String options;
    private String defaultValue;
    private String textColor;
    private String fieldBackgroundColor;
    private String borderColor;
    private Integer borderRadius;
    private Integer fontWeight;
    private ModelEnums.ProductStatus status;
    private ModelEnums.CustomFieldType fieldType;
    private String formula;
    private Set<String> dependentFieldKeys;
    private Date createdAt;
    private Date updatedAt;
}
