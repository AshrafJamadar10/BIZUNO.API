package com.bizuno.models.main;

import com.bizuno.constants.RegexPatterns;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "crud_role_permissions_mapping",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "crud_role_permission_id")
    )
    private Set<RoleCrudPermission> crudPermissions = new HashSet<>();

    public void addPermission(RoleCrudPermission crudPermission) {
        this.crudPermissions.add(crudPermission);
    }

    public void removePermission(RoleCrudPermission crudPermission) {
        this.crudPermissions.remove(crudPermission);
    }
}

