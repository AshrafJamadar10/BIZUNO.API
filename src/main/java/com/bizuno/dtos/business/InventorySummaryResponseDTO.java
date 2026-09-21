package com.bizuno.dtos.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryResponseDTO {
    private long totalProducts;
    private long totalUnits;
    private long lowStock;
    private long outOfStock;
    private BigDecimal stockValue;
}
