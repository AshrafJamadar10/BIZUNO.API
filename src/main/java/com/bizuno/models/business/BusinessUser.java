package com.bizuno.models.business;

import com.bizuno.constants.RegexPatterns;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "business_users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @NotBlank(message = "First Name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "first name should contain only letters and spaces")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "last name should contain only letters and spaces")
    private String lastName;

    @NotBlank(message = "Phone Number is required")
    @Column(unique = true)
    @Pattern(regexp = RegexPatterns.REGEX_PHONE, message = "Phone number should contain exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Email ID is required")
    @Column(unique = true)
    @Pattern( regexp = RegexPatterns.REGEX_EMAIL, message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @JsonIgnore
    private String password;

    @JsonIgnore
    private String refreshToken;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @JsonManagedReference
    private BusinessRole role;
}
