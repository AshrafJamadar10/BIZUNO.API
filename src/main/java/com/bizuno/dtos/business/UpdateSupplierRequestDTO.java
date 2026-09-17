package com.bizuno.dtos.business;

import com.bizuno.constants.RegexPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateSupplierRequestDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Pattern(regexp = RegexPatterns.REGEX_PHONE, message = "Phone number should be valid")
    private String phone;

    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    @Pattern(regexp = RegexPatterns.REGEX_ADDRESS, message = "Address should be valid")
    private String address;

    @Size(max = 15, message = "GST number must not exceed 15 characters")
    private String gstNumber;
}
