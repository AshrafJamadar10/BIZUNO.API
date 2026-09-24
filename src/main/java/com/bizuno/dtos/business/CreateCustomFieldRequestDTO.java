package com.bizuno.dtos.business;

import com.bizuno.enums.ModelEnums;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class CreateCustomFieldRequestDTO {

    @NotBlank(message = "Label is required")
    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;

    @NotBlank(message = "Field key is required")
    @Size(max = 50, message = "Field key must not exceed 50 characters")
    private String fieldKey;

    @Size(max = 255, message = "Placeholder must not exceed 255 characters")
    private String placeholder;

    @Size(max = 500, message = "Helper text must not exceed 500 characters")
    private String helperText;

    private Boolean required = false;

    @NotNull(message = "Data type is required")
    private ModelEnums.CustomFieldDataType dataType;

    @Size(max = 1000, message = "Options must not exceed 1000 characters")
    private String options;

    private String defaultValue;

    private String textColor = "inherit";

    private String fieldBackgroundColor = "inherit";

    private String borderColor = "inherit";

    private Integer borderRadius = 0;

    private Integer fontWeight = 400;

    @NotNull(message = "Status is required")
    private ModelEnums.ProductStatus status;

    @NotNull(message = "Custom field type is required")
    private ModelEnums.CustomFieldType customFieldType;

    private String formula;

    private Set<String> dependentFieldKeys;
}
