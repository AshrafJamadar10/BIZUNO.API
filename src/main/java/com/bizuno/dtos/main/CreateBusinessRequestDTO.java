package com.bizuno.dtos.main;

import com.bizuno.constants.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateBusinessRequestDTO {
    @NotBlank(message = "Business name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_NUMBERS_AND_SPACES, message = "Business name should contain only letters, numbers, and spaces")
    private String businessName;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = RegexPatterns.REGEX_EMAIL, message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = RegexPatterns.REGEX_PHONE, message = "Phone number should be valid")
    private String phone;
}
