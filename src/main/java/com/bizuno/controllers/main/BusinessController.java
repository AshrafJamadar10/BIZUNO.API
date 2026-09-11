package com.bizuno.controllers.main;

import com.bizuno.dtos.business.CreateTenantRequestDTO;
import com.bizuno.models.main.Business;
import com.bizuno.services.main.BusinessService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenant")
@AllArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    public ResponseEntity<Business> createTenant(@RequestBody CreateTenantRequestDTO createTenantRequestDTO) {
        return businessService.createTenant(createTenantRequestDTO);
    }

    @GetMapping
    public ResponseEntity<List<Business>> getAllTenants() {
        return businessService.getAllTenants();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID id) {
        return businessService.deleteTenant(id);
    }
}
