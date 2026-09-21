package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateSupplierRequestDTO;
import com.bizuno.dtos.business.UpdateSupplierRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAuthority('SUPPLIER_CREATE')")
    public ResponseEntity<?> createSupplier(@RequestBody CreateSupplierRequestDTO request,
                                             @PathVariable String businessCode,
                                             @RequestAttribute UserDO userDO) {
        CommonResponse response = supplierService.createSupplier(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SUPPLIER_READ')")
    public ResponseEntity<?> getAllSuppliers(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String sortBy,
                                             @RequestParam(required = false) String sortDirection,
                                             @PathVariable String businessCode,
                                             @RequestAttribute UserDO userDO) {
        CommonResponse response = supplierService.getAllSuppliers(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{supplierId}")
    @PreAuthorize("hasAuthority('SUPPLIER_READ')")
    public ResponseEntity<?> getSupplierById(@PathVariable UUID supplierId,
                                            @PathVariable String businessCode,
                                            @RequestAttribute UserDO userDO) {
        CommonResponse response = supplierService.getSupplierById(supplierId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{supplierId}")
    @PreAuthorize("hasAuthority('SUPPLIER_UPDATE')")
    public ResponseEntity<?> updateSupplier(@PathVariable UUID supplierId,
                                           @RequestBody UpdateSupplierRequestDTO request,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = supplierService.updateSupplier(supplierId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{supplierId}")
    @PreAuthorize("hasAuthority('SUPPLIER_DELETE')")
    public ResponseEntity<?> deleteSupplier(@PathVariable UUID supplierId,
                                            @PathVariable String businessCode,
                                            @RequestAttribute UserDO userDO) {
        CommonResponse response = supplierService.deleteSupplier(supplierId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
