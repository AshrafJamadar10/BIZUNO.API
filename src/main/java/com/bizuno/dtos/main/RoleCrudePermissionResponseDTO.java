package com.bizuno.dtos.main;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class RoleCrudePermissionResponseDTO {
    private String scope;

    @Builder.Default
    private Set<String> operations = new HashSet<>();
}
