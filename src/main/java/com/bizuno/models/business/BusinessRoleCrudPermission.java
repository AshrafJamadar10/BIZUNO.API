package com.bizuno.models.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "crud_role_permissions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessRoleCrudPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crud_role_permission_id")
    private UUID crudRolePermissionId;

    @NotBlank(message = "Scope is required")
    private String scope;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable( name = "crud_operations", joinColumns = @JoinColumn(name = "crud_role_permission_id"))
    @Column(name = "operation", nullable = false)
    private Set<String> operations = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonBackReference
    private BusinessRole role;
}
