package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateCustomerRequestDTO;
import com.bizuno.dtos.business.UpdateCustomerRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomerRequestDTO request,
                                             @PathVariable String businessCode,
                                             @RequestAttribute UserDO userDO) {
        CommonResponse response = customerService.createCustomer(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String sortBy,
                                             @RequestParam(required = false) String sortDirection,
                                             @PathVariable String businessCode,
                                             @RequestAttribute UserDO userDO) {
        CommonResponse response = customerService.getAllCustomers(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable UUID customerId,
                                            @PathVariable String businessCode,
                                            @RequestAttribute UserDO userDO) {
        CommonResponse response = customerService.getCustomerById(customerId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(@PathVariable UUID customerId,
                                           @RequestBody UpdateCustomerRequestDTO request,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = customerService.updateCustomer(customerId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID customerId,
                                            @PathVariable String businessCode,
                                            @RequestAttribute UserDO userDO) {
        CommonResponse response = customerService.deleteCustomer(customerId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
