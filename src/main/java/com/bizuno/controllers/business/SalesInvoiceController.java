package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateSalesInvoiceRequestDTO;
import com.bizuno.dtos.business.UpdateSalesInvoiceRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.SalesInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/sales-invoice")
@RequiredArgsConstructor
public class SalesInvoiceController {

    private final SalesInvoiceService salesInvoiceService;

    @PostMapping
    public ResponseEntity<?> createSalesInvoice(@RequestBody CreateSalesInvoiceRequestDTO request,
                                                  @PathVariable String businessCode,
                                                  @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.createSalesInvoice(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllSalesInvoices(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) String sortBy,
                                                 @RequestParam(required = false) String sortDirection,
                                                 @PathVariable String businessCode,
                                                 @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.getAllSalesInvoices(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{salesInvoiceId}")
    public ResponseEntity<?> getSalesInvoiceById(@PathVariable UUID salesInvoiceId,
                                                @PathVariable String businessCode,
                                                @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.getSalesInvoiceById(salesInvoiceId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{salesInvoiceId}")
    public ResponseEntity<?> updateSalesInvoice(@PathVariable UUID salesInvoiceId,
                                               @RequestBody UpdateSalesInvoiceRequestDTO request,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.updateSalesInvoice(salesInvoiceId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{salesInvoiceId}")
    public ResponseEntity<?> deleteSalesInvoice(@PathVariable UUID salesInvoiceId,
                                                @PathVariable String businessCode,
                                                @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.deleteSalesInvoice(salesInvoiceId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
