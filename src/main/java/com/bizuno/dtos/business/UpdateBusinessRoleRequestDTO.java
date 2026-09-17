package com.bizuno.dtos.business;

import com.bizuno.constants.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateBusinessRoleRequestDTO {
    @NotBlank(message = "Name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_NUMBERS_UNDERSCORE, message = "Name must contain only letters, numbers and underscores")
    @Size(min = 3, max = 20, message = "Name must be between 3-20 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "Description must contain only letters and spaces")
    @Size(min = 5, max = 100, message = "Description must be between 5-100 characters")
    private String description;

    @NotBlank(message = "Role type is required")
    private String type;

    @NotBlank(message = "Title is required")
    private String title;
}
