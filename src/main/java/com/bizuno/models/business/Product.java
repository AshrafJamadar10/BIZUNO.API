package com.bizuno.models.business;

import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 50, message = "SKU must not exceed 50 characters")
    @Column(unique = true)
    private String sku;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase price must be greater than or equal to 0")
    private BigDecimal purchasePrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price must be greater than or equal to 0")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be greater than or equal to 0")
    private BigDecimal taxRate;

    private Integer minStock = 0;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.ProductStatus status = ModelEnums.ProductStatus.ACTIVE;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Inventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SalesInvoiceItem> salesInvoiceItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CustomFieldValue> customFieldValues = new ArrayList<>();
}
