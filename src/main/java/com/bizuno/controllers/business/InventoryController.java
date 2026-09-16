package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateInventoryRequestDTO;
import com.bizuno.dtos.business.UpdateInventoryRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/bizuno/business/{businessCode}/inventory")
@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<?> createInventoryMovement(@RequestBody CreateInventoryRequestDTO request,
                                                     @PathVariable String businessCode,
                                                     @RequestAttribute UserDO userDO) {
        CommonResponse response = inventoryService.createInventoryMovement(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllInventoryMovements(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String sortBy,
                                                      @RequestParam(required = false) String sortDirection,
                                                      @PathVariable String businessCode,
                                                      @RequestAttribute UserDO userDO) {
        CommonResponse response = inventoryService.getAllInventoryMovements(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{inventoryMovementId}")
    public ResponseEntity<?> getInventoryMovementById(@PathVariable UUID inventoryMovementId,
                                                     @PathVariable String businessCode,
                                                     @RequestAttribute UserDO userDO) {
        CommonResponse response = inventoryService.getInventoryMovementById(inventoryMovementId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{inventoryMovementId}")
    public ResponseEntity<?> updateInventoryMovement(@PathVariable UUID inventoryMovementId,
                                                    @RequestBody UpdateInventoryRequestDTO request,
                                                    @PathVariable String businessCode,
                                                    @RequestAttribute UserDO userDO) {
        CommonResponse response = inventoryService.updateInventoryMovement(inventoryMovementId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{inventoryMovementId}")
    public ResponseEntity<?> deleteInventoryMovement(@PathVariable UUID inventoryMovementId,
                                                    @PathVariable String businessCode,
                                                    @RequestAttribute UserDO userDO) {
        CommonResponse response = inventoryService.deleteInventoryMovement(inventoryMovementId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getInventoryByProduct(@PathVariable UUID productId,
                                                   @PathVariable String businessCode,
                                                   @RequestAttribute UserDO userDO) {
        CommonResponse response = inventoryService.getInventoryByProduct(productId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<?> getInventoryByWarehouse(@PathVariable UUID warehouseId,
                                                     @PathVariable String businessCode,
                                                     @RequestAttribute UserDO userDO) {
        CommonResponse response = inventoryService.getInventoryByWarehouse(warehouseId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
