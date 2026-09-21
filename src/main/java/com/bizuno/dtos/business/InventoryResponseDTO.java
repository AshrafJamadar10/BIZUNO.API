package com.bizuno.dtos.business;

import com.bizuno.enums.ModelEnums;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
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
    private String productUnit;
    private Integer minStock;
    private java.math.BigDecimal purchasePrice;
    private UUID warehouseId;
    private String warehouseName;
    private ModelEnums.MovementType movementType;
    private Integer quantity;
    private String note;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
}
