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

    @NotBlank(message = "First Name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "first name should contain only letters and spaces")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "last name should contain only letters and spaces")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = RegexPatterns.REGEX_EMAIL, message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = RegexPatterns.REGEX_PHONE, message = "Phone number should be valid")
    private String phone;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = RegexPatterns.REGEX_PASSWORD, message = "Password should be valid")
    private String password;
}
