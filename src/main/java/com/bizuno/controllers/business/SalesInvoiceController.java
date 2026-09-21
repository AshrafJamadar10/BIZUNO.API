package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateSalesInvoiceRequestDTO;
import com.bizuno.dtos.business.UpdateSalesInvoiceRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.SalesInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/sales-invoice")
@RequiredArgsConstructor
public class SalesInvoiceController {

    private final SalesInvoiceService salesInvoiceService;

    @PostMapping
    @PreAuthorize("hasAuthority('SALES_INVOICE_CREATE')")
    public ResponseEntity<?> createSalesInvoice(@RequestBody CreateSalesInvoiceRequestDTO request,
                                                  @PathVariable String businessCode,
                                                  @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.createSalesInvoice(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALES_INVOICE_READ')")
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
    @PreAuthorize("hasAuthority('SALES_INVOICE_READ')")
    public ResponseEntity<?> getSalesInvoiceById(@PathVariable UUID salesInvoiceId,
                                                @PathVariable String businessCode,
                                                @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.getSalesInvoiceById(salesInvoiceId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{salesInvoiceId}")
    @PreAuthorize("hasAuthority('SALES_INVOICE_UPDATE')")
    public ResponseEntity<?> updateSalesInvoice(@PathVariable UUID salesInvoiceId,
                                               @RequestBody UpdateSalesInvoiceRequestDTO request,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.updateSalesInvoice(salesInvoiceId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{salesInvoiceId}")
    @PreAuthorize("hasAuthority('SALES_INVOICE_DELETE')")
    public ResponseEntity<?> deleteSalesInvoice(@PathVariable UUID salesInvoiceId,
                                                @PathVariable String businessCode,
                                                @RequestAttribute UserDO userDO) {
        CommonResponse response = salesInvoiceService.deleteSalesInvoice(salesInvoiceId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
