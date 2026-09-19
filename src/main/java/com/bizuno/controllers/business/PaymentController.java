package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreatePaymentRequestDTO;
import com.bizuno.dtos.business.UpdatePaymentRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENTS_CREATE')")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequestDTO request,
                                            @PathVariable String businessCode,
                                            @RequestAttribute UserDO userDO) {
        CommonResponse response = paymentService.createPayment(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PAYMENTS_READ')")
    public ResponseEntity<?> getAllPayments(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(required = false) String sortDirection,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = paymentService.getAllPayments(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('PAYMENTS_READ')")
    public ResponseEntity<?> getPaymentById(@PathVariable UUID paymentId,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = paymentService.getPaymentById(paymentId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('PAYMENTS_UPDATE')")
    public ResponseEntity<?> updatePayment(@PathVariable UUID paymentId,
                                         @RequestBody UpdatePaymentRequestDTO request,
                                         @PathVariable String businessCode,
                                         @RequestAttribute UserDO userDO) {
        CommonResponse response = paymentService.updatePayment(paymentId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('PAYMENTS_DELETE')")
    public ResponseEntity<?> deletePayment(@PathVariable UUID paymentId,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = paymentService.deletePayment(paymentId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
