package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateWarehouseRequestDTO;
import com.bizuno.dtos.business.UpdateWarehouseRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/bizuno/business/{businessCode}/warehouse")
@RestController
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_CREATE')")
    public ResponseEntity<?> createWarehouse(@RequestBody CreateWarehouseRequestDTO request,
                                            @PathVariable String businessCode,
                                            @RequestAttribute UserDO userDO) {
        CommonResponse response = warehouseService.createWarehouse(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_READ')")
    public ResponseEntity<?> getAllWarehouses(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String sortBy,
                                             @RequestParam(required = false) String sortDirection,
                                             @PathVariable String businessCode,
                                             @RequestAttribute UserDO userDO) {
        CommonResponse response = warehouseService.getAllWarehouses(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{warehouseId}")
    @PreAuthorize("hasAuthority('WAREHOUSE_READ')")
    public ResponseEntity<?> getWarehouseById(@PathVariable UUID warehouseId,
                                             @PathVariable String businessCode,
                                             @RequestAttribute UserDO userDO) {
        CommonResponse response = warehouseService.getWarehouseById(warehouseId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{warehouseId}")
    @PreAuthorize("hasAuthority('WAREHOUSE_UPDATE')")
    public ResponseEntity<?> updateWarehouse(@PathVariable UUID warehouseId,
                                            @RequestBody UpdateWarehouseRequestDTO request,
                                            @PathVariable String businessCode,
                                            @RequestAttribute UserDO userDO) {
        CommonResponse response = warehouseService.updateWarehouse(warehouseId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{warehouseId}")
    @PreAuthorize("hasAuthority('WAREHOUSE_DELETE')")
    public ResponseEntity<?> deleteWarehouse(@PathVariable UUID warehouseId,
                                            @PathVariable String businessCode,
                                            @RequestAttribute UserDO userDO) {
        CommonResponse response = warehouseService.deleteWarehouse(warehouseId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
