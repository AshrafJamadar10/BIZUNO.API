package com.bizuno.dtos.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRoleResponseDTO {
    private UUID roleId;
    private String name;
    private String description;
    private String type;
    private String title;
    private Set<BusinessRoleCrudPermissionResponseDTO> crudPermissions;
}
