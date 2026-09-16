package com.bizuno.models.business;

import com.bizuno.constants.RegexPatterns;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.BaseEntity;
import com.bizuno.models.main.Business;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "supplier")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID supplierId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Pattern(regexp = RegexPatterns.REGEX_PHONE, message = "Phone number should be valid")
    private String phone;

    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    @Pattern(regexp = RegexPatterns.REGEX_ADDRESS, message = "Address should be valid")
    private String address;

    @Size(max = 15, message = "GST number must not exceed 15 characters")
    private String gstNumber;

    @Builder.Default
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.SupplierStatus status = ModelEnums.SupplierStatus.ACTIVE;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
}
