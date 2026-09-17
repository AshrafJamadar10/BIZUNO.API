package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateBusinessUserRequestDTO;
import com.bizuno.dtos.business.UpdateBusinessUserRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.BusinessUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/business-user")
@RequiredArgsConstructor
public class BusinessUserController {

    private final BusinessUserService businessUserService;

    @PostMapping
    public ResponseEntity<?> createBusinessUser(@RequestBody CreateBusinessUserRequestDTO request,
                                                 @PathVariable String businessCode,
                                                 @RequestAttribute UserDO userDO) {
        CommonResponse response = businessUserService.createBusinessUser(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllBusinessUsers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) String sortBy,
                                                 @RequestParam(required = false) String sortDirection,
                                                 @PathVariable String businessCode,
                                                 @RequestAttribute UserDO userDO) {
        CommonResponse response = businessUserService.getAllBusinessUsers(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getBusinessUserById(@PathVariable UUID userId,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = businessUserService.getBusinessUserById(userId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateBusinessUser(@PathVariable UUID userId,
                                              @RequestBody UpdateBusinessUserRequestDTO request,
                                              @PathVariable String businessCode,
                                              @RequestAttribute UserDO userDO) {
        CommonResponse response = businessUserService.updateBusinessUser(userId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteBusinessUser(@PathVariable UUID userId,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = businessUserService.deleteBusinessUser(userId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
