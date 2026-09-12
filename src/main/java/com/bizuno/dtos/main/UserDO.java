package com.bizuno.dtos.main;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDO {
    private UUID userId;
    private String phoneOrEmail;
    private String userType;
    private RoleResponseDTO role;
    private String businessCode;
    private Set<String> packageScopes;
}