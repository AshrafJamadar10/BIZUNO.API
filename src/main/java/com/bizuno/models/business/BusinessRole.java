package com.bizuno.models.business;

import com.bizuno.constants.RegexPatterns;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID roleId;

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

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<BusinessRoleCrudPermission> crudPermissions = new HashSet<>();

    public void addPermission(BusinessRoleCrudPermission permission) {
        permission.setRole(this);
        this.crudPermissions.add(permission);
    }
}
