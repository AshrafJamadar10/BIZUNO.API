package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateStaffRequestDTO;
import com.bizuno.dtos.business.UpdateStaffRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @PreAuthorize("hasAuthority('STAFF_CREATE')")
    public ResponseEntity<?> createStaff(@RequestBody CreateStaffRequestDTO request,
                                         @PathVariable String businessCode,
                                         @RequestAttribute UserDO userDO) {
        CommonResponse response = staffService.createStaff(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STAFF_READ')")
    public ResponseEntity<?> getAllStaff(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String sortBy,
                                          @RequestParam(required = false) String sortDirection,
                                          @PathVariable String businessCode,
                                          @RequestAttribute UserDO userDO) {
        CommonResponse response = staffService.getAllStaff(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_READ')")
    public ResponseEntity<?> getStaffById(@PathVariable UUID staffId,
                                         @PathVariable String businessCode,
                                         @RequestAttribute UserDO userDO) {
        CommonResponse response = staffService.getStaffById(staffId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    public ResponseEntity<?> updateStaff(@PathVariable UUID staffId,
                                        @RequestBody UpdateStaffRequestDTO request,
                                        @PathVariable String businessCode,
                                        @RequestAttribute UserDO userDO) {
        CommonResponse response = staffService.updateStaff(staffId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_DELETE')")
    public ResponseEntity<?> deleteStaff(@PathVariable UUID staffId,
                                         @PathVariable String businessCode,
                                         @RequestAttribute UserDO userDO) {
        CommonResponse response = staffService.deleteStaff(staffId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
