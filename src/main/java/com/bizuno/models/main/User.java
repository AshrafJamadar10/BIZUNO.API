package com.bizuno.models.main;

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
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID userId;

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
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonManagedReference
    private Role role;
}
