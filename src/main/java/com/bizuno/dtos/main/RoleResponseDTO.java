package com.bizuno.dtos.main;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RoleResponseDTO {
    private UUID roleId;
    private String name;
    private String description;
    private String title;
    private String type;

    @Builder.Default
    private Set<RoleCrudePermissionResponseDTO> crudPermissions = new HashSet<>();
}
