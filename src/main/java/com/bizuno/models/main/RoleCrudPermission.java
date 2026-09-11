package com.bizuno.models.main;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "crud_role_permissions")
@Getter
@Setter
@NoArgsConstructor
public class RoleCrudPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crud_role_permission_id")
    private UUID crudRolePermissionId;

    @NotBlank(message = "Scope is required")
    private String scope;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable( name = "crud_operations", joinColumns = @JoinColumn(name = "crud_role_permission_id"))
    @Column(name = "operation", nullable = false)
    private Set<String> operations = new HashSet<>();
}

