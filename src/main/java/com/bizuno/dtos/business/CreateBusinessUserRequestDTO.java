package com.bizuno.dtos.business;

import com.bizuno.constants.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateBusinessUserRequestDTO {
    @NotBlank(message = "First Name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "first name should contain only letters and spaces")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "last name should contain only letters and spaces")
    private String lastName;

    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = RegexPatterns.REGEX_PHONE, message = "Phone number should contain exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Email ID is required")
    @Pattern(regexp = RegexPatterns.REGEX_EMAIL, message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private UUID roleId;
}
