package com.bizuno.utils;

import com.bizuno.models.business.BusinessRoleCrudPermission;
import com.bizuno.dtos.main.RoleCrudePermissionResponseDTO;
import com.bizuno.dtos.main.RoleResponseDTO;
import com.bizuno.models.business.BusinessRole;
import com.bizuno.models.main.Role;
import com.bizuno.models.main.RoleCrudPermission;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleFormatterForUI {

    public RoleResponseDTO formatRole(Role role) {
        if(role == null) return null;

        Set<RoleCrudePermissionResponseDTO> permissionResponseDTOS =
                role.getCrudPermissions()
                        .stream()
                        .map(this::mapPermission)
                        .collect(Collectors.toSet());

        return RoleResponseDTO.builder()
                .roleId(role.getRoleId())
                .name(role.getName())
                .title(role.getTitle())
                .type(role.getType())
                .description(role.getDescription())
                .crudPermissions(permissionResponseDTOS)
                .build();
    }

    public RoleResponseDTO formatRole(BusinessRole role) {
        if(role == null) return null;

        Set<RoleCrudePermissionResponseDTO> permissionResponseDTOS =
                role.getCrudPermissions()
                        .stream()
                        .map(this::mapPermission)
                        .collect(Collectors.toSet());

        return RoleResponseDTO.builder()
                .roleId(role.getRoleId())
                .name(role.getName())
                .title(role.getTitle())
                .type(role.getType())
                .description(role.getDescription())
                .crudPermissions(permissionResponseDTOS)
                .build();
    }

    // Helper methods

    private RoleCrudePermissionResponseDTO mapPermission(RoleCrudPermission permission) {
        return RoleCrudePermissionResponseDTO.builder()
                .scope(permission.getScope())
                .operations(permission.getOperations())
                .build();
    }

    private RoleCrudePermissionResponseDTO mapPermission(BusinessRoleCrudPermission permission) {
        return RoleCrudePermissionResponseDTO.builder()
                .scope(permission.getScope())
                .operations(permission.getOperations())
                .build();
    }
}

