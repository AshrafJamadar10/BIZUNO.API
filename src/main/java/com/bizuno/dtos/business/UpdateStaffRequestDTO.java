package com.bizuno.dtos.business;

import com.bizuno.constants.RegexPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UpdateStaffRequestDTO {
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Pattern(regexp = RegexPatterns.REGEX_PHONE, message = "Phone should be valid")
    private String phone;

    @Size(max = 50, message = "Department must not exceed 50 characters")
    private String department;

    @Size(max = 50, message = "Designation must not exceed 50 characters")
    private String designation;

    private UUID roleId;
}
