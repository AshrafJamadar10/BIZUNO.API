package com.bizuno.dtos.business;

import com.bizuno.constants.RegexPatterns;
import com.bizuno.enums.ModelEnums;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class CreateProductRequestDTO {
    private UUID categoryId;

    private UUID warehouseId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_NUMBERS_AND_SPACES, message = "Name should contain only letters, numbers, and spaces")
    private String name;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Pattern(regexp = RegexPatterns.REGEX_DESCRIPTION, message = "Description should contain only letters, numbers, and spaces")
    private String description;

    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase price must be greater than or equal to 0")
    private BigDecimal purchasePrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price must be greater than or equal to 0")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be greater than or equal to 0")
    private BigDecimal taxRate;

    private Integer minStock = 0;

    @NotNull(message = "Status is required")
    private ModelEnums.ProductStatus status;
}
