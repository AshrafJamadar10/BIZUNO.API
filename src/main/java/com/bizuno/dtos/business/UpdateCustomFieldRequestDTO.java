package com.bizuno.dtos.business;

import com.bizuno.enums.ModelEnums;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UpdateCustomFieldRequestDTO {

    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;

    @Size(max = 50, message = "Field key must not exceed 50 characters")
    private String fieldKey;

    @Size(max = 255, message = "Placeholder must not exceed 255 characters")
    private String placeholder;

    @Size(max = 500, message = "Helper text must not exceed 500 characters")
    private String helperText;

    private Boolean required;

    private ModelEnums.CustomFieldDataType dataType;

    @Size(max = 1000, message = "Options must not exceed 1000 characters")
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
}
