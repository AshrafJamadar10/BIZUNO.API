package com.bizuno.dtos.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {
    private UUID supplierId;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String city;
    private String address;
    private String gstNumber;
    private BigDecimal outstandingBalance;
    private String status;
}
