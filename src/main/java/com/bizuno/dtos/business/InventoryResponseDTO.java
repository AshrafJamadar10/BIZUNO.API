package com.bizuno.dtos.business;

import com.bizuno.enums.ModelEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {
    private UUID inventoryMovementId;
    private UUID productId;
    private String productName;
    private String productSku;
    private UUID warehouseId;
    private String warehouseName;
    private ModelEnums.MovementType movementType;
    private Integer quantity;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
