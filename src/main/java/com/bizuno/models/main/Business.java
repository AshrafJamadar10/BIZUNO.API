package com.bizuno.models.main;

import com.bizuno.constants.RegexPatterns;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "business")
public class Business extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID businessId;

    @NotBlank(message = "Business name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_NUMBERS_AND_SPACES, message = "Business name should contain only letters, numbers, and spaces")
    private String businessName;

    @NotBlank(message = "Business code is required")
    @Column(unique = true)
    private String businessCode;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "First name should contain only letters and spaces")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = RegexPatterns.REGEX_LETTERS_AND_SPACES, message = "Last name should contain only letters and spaces")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = RegexPatterns.REGEX_EMAIL, message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = RegexPatterns.REGEX_PHONE, message = "Phone number should be valid")
    private String phone;

    private String logo;

    @NotBlank(message = "Tenant ID is required")
    @Column(name = "tenant_id",nullable = false, unique = true)
    private String tenantId;

    @NotBlank(message = "Database name is required")
    @Column(name = "db_name",nullable = false, unique = true)
    private String dbName;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Subscription> subscriptions = new ArrayList<>();
}
