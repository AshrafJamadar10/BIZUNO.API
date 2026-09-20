package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateCategoryRequestDTO;
import com.bizuno.dtos.business.UpdateCategoryRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CATEGORY_CREATE')")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequestDTO request,
                                         @PathVariable String businessCode,
                                         @RequestAttribute UserDO userDO) {
        CommonResponse response = categoryService.createCategory(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_CATEGORY_READ')")
    public ResponseEntity<?> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String sortBy,
                                              @RequestParam(required = false) String sortDirection,
                                              @PathVariable String businessCode,
                                              @RequestAttribute UserDO userDO) {
        CommonResponse response = categoryService.getAllCategories(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('PRODUCT_CATEGORY_READ')")
    public ResponseEntity<?> getCategoryById(@PathVariable UUID categoryId,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = categoryService.getCategoryById(categoryId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('PRODUCT_CATEGORY_UPDATE')")
    public ResponseEntity<?> updateCategory(@PathVariable UUID categoryId,
                                           @RequestBody UpdateCategoryRequestDTO request,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = categoryService.updateCategory(categoryId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('PRODUCT_CATEGORY_DELETE')")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID categoryId,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = categoryService.deleteCategory(categoryId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
