package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreatePurchaseOrderRequestDTO;
import com.bizuno.dtos.business.UpdatePurchaseOrderRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<?> createPurchaseOrder(@RequestBody CreatePurchaseOrderRequestDTO request,
                                                  @PathVariable String businessCode,
                                                  @RequestAttribute UserDO userDO) {
        CommonResponse response = purchaseOrderService.createPurchaseOrder(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllPurchaseOrders(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String sortBy,
                                                  @RequestParam(required = false) String sortDirection,
                                                  @PathVariable String businessCode,
                                                  @RequestAttribute UserDO userDO) {
        CommonResponse response = purchaseOrderService.getAllPurchaseOrders(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{purchaseOrderId}")
    public ResponseEntity<?> getPurchaseOrderById(@PathVariable UUID purchaseOrderId,
                                                  @PathVariable String businessCode,
                                                  @RequestAttribute UserDO userDO) {
        CommonResponse response = purchaseOrderService.getPurchaseOrderById(purchaseOrderId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{purchaseOrderId}")
    public ResponseEntity<?> updatePurchaseOrder(@PathVariable UUID purchaseOrderId,
                                               @RequestBody UpdatePurchaseOrderRequestDTO request,
                                               @PathVariable String businessCode,
                                               @RequestAttribute UserDO userDO) {
        CommonResponse response = purchaseOrderService.updatePurchaseOrder(purchaseOrderId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{purchaseOrderId}")
    public ResponseEntity<?> deletePurchaseOrder(@PathVariable UUID purchaseOrderId,
                                                  @PathVariable String businessCode,
                                                  @RequestAttribute UserDO userDO) {
        CommonResponse response = purchaseOrderService.deletePurchaseOrder(purchaseOrderId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
