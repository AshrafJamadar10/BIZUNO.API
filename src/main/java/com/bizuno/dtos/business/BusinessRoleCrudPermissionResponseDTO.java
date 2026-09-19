package com.bizuno.dtos.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRoleCrudPermissionResponseDTO {
    private UUID permissionId;
    private String scope;
    private Set<String> operations = new HashSet<>();
}
