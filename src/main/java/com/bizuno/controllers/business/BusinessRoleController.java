package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateBusinessRoleRequestDTO;
import com.bizuno.dtos.business.UpdateBusinessRoleRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.BusinessRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/business-role")
@RequiredArgsConstructor
public class BusinessRoleController {

    private final BusinessRoleService businessRoleService;

    @PostMapping
    public ResponseEntity<?> createBusinessRole(@RequestBody CreateBusinessRoleRequestDTO request,
                                                 @PathVariable String businessCode,
                                                 @RequestAttribute UserDO userDO) {
        CommonResponse response = businessRoleService.createBusinessRole(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllBusinessRoles(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) String sortBy,
                                                 @RequestParam(required = false) String sortDirection,
                                                 @PathVariable String businessCode,
                                                 @RequestAttribute UserDO userDO) {
        CommonResponse response = businessRoleService.getAllBusinessRoles(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<?> getBusinessRoleById(@PathVariable UUID roleId,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = businessRoleService.getBusinessRoleById(roleId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<?> updateBusinessRole(@PathVariable UUID roleId,
                                              @RequestBody UpdateBusinessRoleRequestDTO request,
                                              @PathVariable String businessCode,
                                              @RequestAttribute UserDO userDO) {
        CommonResponse response = businessRoleService.updateBusinessRole(roleId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteBusinessRole(@PathVariable UUID roleId,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = businessRoleService.deleteBusinessRole(roleId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
