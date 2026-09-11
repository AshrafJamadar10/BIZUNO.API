package com.bizuno.controllers.main;

import com.bizuno.dtos.main.CreateBusinessRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.services.main.BusinessService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/api/business")
@AllArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    public ResponseEntity<?> createBusiness(@RequestBody @Valid CreateBusinessRequestDTO createBusinessRequestDTO) {
        CommonResponse response = businessService.createBusiness(createBusinessRequestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{businessId}")
    public ResponseEntity<?> updateBusiness(@PathVariable UUID businessId,
                                          @RequestBody @Valid CreateBusinessRequestDTO createBusinessRequestDTO) {
        CommonResponse response = businessService.updateBusiness(businessId, createBusinessRequestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllBusiness(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(required = false) String sortDirection) {
        CommonResponse response = businessService.getAllBusiness(page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<?> getBusiness(@PathVariable UUID businessId) {
        CommonResponse response = businessService.getBusiness(businessId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBusiness(@PathVariable UUID businessId) {
        CommonResponse response = businessService.deleteBusiness(businessId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
