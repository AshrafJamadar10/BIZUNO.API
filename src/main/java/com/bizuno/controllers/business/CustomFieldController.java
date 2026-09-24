package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateCustomFieldRequestDTO;
import com.bizuno.dtos.business.UpdateCustomFieldRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.CustomFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/custom-field")
@RequiredArgsConstructor
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    @PostMapping
    @PreAuthorize("hasAuthority('SETTING_UPDATE')")
    public ResponseEntity<?> createCustomField(@RequestBody CreateCustomFieldRequestDTO request,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = customFieldService.createCustomField(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SETTING_READ')")
    public ResponseEntity<?> getAllCustomFields(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) String sortBy,
                                                 @RequestParam(required = false) String sortDirection,
                                                 @PathVariable String businessCode,
                                                 @RequestAttribute UserDO userDO) {
        CommonResponse response = customFieldService.getAllCustomFields(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('SETTING_READ')")
    public ResponseEntity<?> getActiveCustomFields(@PathVariable String businessCode,
                                                   @RequestAttribute UserDO userDO) {
        CommonResponse response = customFieldService.getActiveCustomFields(businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{fieldId}")
    @PreAuthorize("hasAuthority('SETTING_READ')")
    public ResponseEntity<?> getCustomFieldById(@PathVariable UUID fieldId,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = customFieldService.getCustomFieldById(fieldId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{fieldId}")
    @PreAuthorize("hasAuthority('SETTING_UPDATE')")
    public ResponseEntity<?> updateCustomField(@PathVariable UUID fieldId,
                                               @RequestBody UpdateCustomFieldRequestDTO request,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = customFieldService.updateCustomField(fieldId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{fieldId}")
    @PreAuthorize("hasAuthority('SETTING_UPDATE')")
    public ResponseEntity<?> deleteCustomField(@PathVariable UUID fieldId,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = customFieldService.deleteCustomField(fieldId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
