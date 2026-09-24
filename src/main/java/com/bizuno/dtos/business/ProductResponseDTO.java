package com.bizuno.dtos.business;

import com.bizuno.enums.ModelEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private UUID productId;
    private UUID categoryId;
    private String name;
    private String sku;
    private String barcode;
    private String description;
    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal taxRate;
    private Integer minStock;
    private ModelEnums.ProductStatus status;
    private List<ProductCustomFieldValueDTO> customFieldValues;
}
