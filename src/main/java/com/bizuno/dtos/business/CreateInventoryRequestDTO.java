package com.bizuno.dtos.business;

import com.bizuno.constants.RegexPatterns;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateInventoryRequestDTO {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    private UUID warehouseId;

    @NotNull(message = "Movement type is required")
    private com.bizuno.enums.ModelEnums.MovementType movementType;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    @Pattern(regexp = RegexPatterns.REGEX_DESCRIPTION, message = "Note should contain only letters, numbers, and spaces")
    private String note;
}
