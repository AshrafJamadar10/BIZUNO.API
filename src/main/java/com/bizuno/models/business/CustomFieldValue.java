package com.bizuno.models.business;

import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "product_custom_field_value")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomFieldValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID valueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    @JsonIgnore
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    @JsonIgnore
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = true)
    @JsonIgnore
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = true)
    @JsonIgnore
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = true)
    @JsonIgnore
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = true)
    @JsonIgnore
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = true)
    @JsonIgnore
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_field_id")
    private CustomField customField;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "formula", columnDefinition = "TEXT")
    private String formula;

    @ElementCollection
    @CollectionTable(name = "custom_field_dependencies",
            joinColumns = @JoinColumn(name = "field_id"))
    @Column(name = "dependency_field_key")
    private Set<String> dependentFieldKeys = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "result_data_type")
    private ModelEnums.CustomFieldDataType resultDataType;
}
