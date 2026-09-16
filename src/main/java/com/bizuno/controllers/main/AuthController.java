package com.bizuno.controllers.main;

import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.CreateBusinessRequestDTO;
import com.bizuno.dtos.main.LoginRequestDTO;
import com.bizuno.services.main.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bizuno/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/super-admin")
    public ResponseEntity<?> loginSuperAdmin(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        CommonResponse response = authService.loginSuperAdmin(loginRequestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/register/business")
    public ResponseEntity<?> register(@RequestBody @Valid CreateBusinessRequestDTO createBusinessRequestDTO) {
        CommonResponse response = authService.register(createBusinessRequestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/business/{businessCode}/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO, @PathVariable String businessCode) {
        CommonResponse response = authService.login(loginRequestDTO, businessCode);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
