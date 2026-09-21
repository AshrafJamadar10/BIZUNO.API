package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateProductRequestDTO;
import com.bizuno.dtos.business.UpdateProductRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequestDTO request,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = productService.createProduct(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(required = false) String sortDirection,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = productService.getAllProducts(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<?> getProductById(@PathVariable UUID productId,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = productService.getProductById(productId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public ResponseEntity<?> updateProduct(@PathVariable UUID productId,
                                           @RequestBody UpdateProductRequestDTO request,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = productService.updateProduct(productId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = productService.deleteProduct(productId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
