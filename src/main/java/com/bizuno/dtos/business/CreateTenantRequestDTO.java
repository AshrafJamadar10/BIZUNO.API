package com.bizuno.dtos.business;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateTenantRequestDTO {
    String name;
    String email;
    String password;
    String dbName;
}
