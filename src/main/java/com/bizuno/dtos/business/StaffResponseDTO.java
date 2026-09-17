package com.bizuno.dtos.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDTO {
    private UUID staffId;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private String status;
    private UUID roleId;
    private String roleName;
}
