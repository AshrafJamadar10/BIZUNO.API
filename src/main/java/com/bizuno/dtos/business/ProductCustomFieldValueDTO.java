package com.bizuno.dtos.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCustomFieldValueDTO {
    private UUID valueId;
    private UUID productId;
    private UUID customFieldId;
    private String fieldKey;
    private String fieldLabel;
    private String value;
}
