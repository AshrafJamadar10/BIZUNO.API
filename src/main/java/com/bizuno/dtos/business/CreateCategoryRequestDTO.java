package com.bizuno.dtos.business;

import com.bizuno.constants.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCategoryRequestDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_NUMBERS_AND_SPACES, message = "Name should contain only letters, numbers, and spaces")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Pattern(regexp = RegexPatterns.REGEX_DESCRIPTION, message = "Description should contain only letters, numbers, and spaces")
    private String description;
}
