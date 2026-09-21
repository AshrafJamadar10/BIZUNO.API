package com.bizuno.controllers.main;

import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.PackageRequestDTO;
import com.bizuno.services.main.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody @Valid PackageRequestDTO packageRequest) {
        CommonResponse response = packageService.createPackage(packageRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{packageId}")
    public ResponseEntity<?> updatePackage(@PathVariable UUID packageId,
                                          @RequestBody @Valid PackageRequestDTO packageRequest) {
        CommonResponse response = packageService.updatePackage(packageId, packageRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllPackages(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(required = false) String sortDirection) {
        CommonResponse response = packageService.getAllPackages(page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{packageId}")
    public ResponseEntity<?> getPackage(@PathVariable UUID packageId) {
        CommonResponse response = packageService.getPackage(packageId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{packageId}")
    public ResponseEntity<?> deletePackage(@PathVariable UUID packageId) {
        CommonResponse response = packageService.deletePackage(packageId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/scopes")
    public ResponseEntity<?> getScopes() {
        CommonResponse response = packageService.getScopes();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
