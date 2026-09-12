package com.bizuno.dtos.main;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {
    private String phoneOrEmail;
    private String password;
}
