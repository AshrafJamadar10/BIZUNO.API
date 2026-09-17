package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateStaffAttendanceRequestDTO;
import com.bizuno.dtos.business.UpdateStaffAttendanceRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.StaffAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/staff-attendance")
@RequiredArgsConstructor
public class StaffAttendanceController {

    private final StaffAttendanceService staffAttendanceService;

    @PostMapping
    public ResponseEntity<?> createStaffAttendance(@RequestBody CreateStaffAttendanceRequestDTO request,
                                                     @PathVariable String businessCode,
                                                     @RequestAttribute UserDO userDO) {
        CommonResponse response = staffAttendanceService.createStaffAttendance(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllStaffAttendance(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) String sortBy,
                                                    @RequestParam(required = false) String sortDirection,
                                                    @PathVariable String businessCode,
                                                    @RequestAttribute UserDO userDO) {
        CommonResponse response = staffAttendanceService.getAllStaffAttendance(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{staffAttendanceId}")
    public ResponseEntity<?> getStaffAttendanceById(@PathVariable UUID staffAttendanceId,
                                                   @PathVariable String businessCode,
                                                   @RequestAttribute UserDO userDO) {
        CommonResponse response = staffAttendanceService.getStaffAttendanceById(staffAttendanceId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{staffAttendanceId}")
    public ResponseEntity<?> updateStaffAttendance(@PathVariable UUID staffAttendanceId,
                                                  @RequestBody UpdateStaffAttendanceRequestDTO request,
                                                  @PathVariable String businessCode,
                                                  @RequestAttribute UserDO userDO) {
        CommonResponse response = staffAttendanceService.updateStaffAttendance(staffAttendanceId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{staffAttendanceId}")
    public ResponseEntity<?> deleteStaffAttendance(@PathVariable UUID staffAttendanceId,
                                                   @PathVariable String businessCode,
                                                   @RequestAttribute UserDO userDO) {
        CommonResponse response = staffAttendanceService.deleteStaffAttendance(staffAttendanceId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
