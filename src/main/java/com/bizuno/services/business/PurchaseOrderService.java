package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreatePurchaseOrderItemRequestDTO;
import com.bizuno.dtos.business.CreatePurchaseOrderRequestDTO;
import com.bizuno.dtos.business.PurchaseOrderItemResponseDTO;
import com.bizuno.dtos.business.PurchaseOrderResponseDTO;
import com.bizuno.dtos.business.UpdatePurchaseOrderRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.Product;
import com.bizuno.models.business.PurchaseOrder;
import com.bizuno.models.business.PurchaseOrderItem;
import com.bizuno.models.business.Supplier;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.ProductRepository;
import com.bizuno.repositories.business.PurchaseOrderItemRepository;
import com.bizuno.repositories.business.PurchaseOrderRepository;
import com.bizuno.repositories.business.SupplierRepository;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.utils.TenantTransactionalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createPurchaseOrder(CreatePurchaseOrderRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PurchaseOrderRepository purchaseOrderRepository = tenantTransactionalUtil.getRepository(entityManager, PurchaseOrderRepository.class);
            SupplierRepository supplierRepository = tenantTransactionalUtil.getRepository(entityManager, SupplierRepository.class);
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);
            PurchaseOrderItemRepository purchaseOrderItemRepository = tenantTransactionalUtil.getRepository(entityManager, PurchaseOrderItemRepository.class);

            if (purchaseOrderRepository.existsByOrderNumber(request.getOrderNumber())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Order with this number"));
            }

            Optional<Supplier> supplierOpt = supplierRepository.findById(request.getSupplierId());
            if (supplierOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Supplier"));
            }

            PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                    .supplier(supplierOpt.get())
                    .orderNumber(request.getOrderNumber())
                    .orderDate(request.getOrderDate())
                    .expectedDate(request.getExpectedDate())
                    .subtotal(request.getSubtotal())
                    .taxAmount(request.getTaxAmount())
                    .total(request.getTotal())
                    .status(request.getStatus())
                    .items(new ArrayList<>())
                    .build();
            purchaseOrder.prePersist();

            PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

            if (request.getItems() != null && !request.getItems().isEmpty()) {
                for (CreatePurchaseOrderItemRequestDTO itemRequest : request.getItems()) {
                    Optional<Product> productOpt = productRepository.findById(itemRequest.getProductId());
                    if (productOpt.isEmpty()) {
                        return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Product"));
                    }

                    PurchaseOrderItem item = PurchaseOrderItem.builder()
                            .purchaseOrder(savedOrder)
                            .product(productOpt.get())
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(itemRequest.getUnitPrice())
                            .discount(itemRequest.getDiscount())
                            .tax(itemRequest.getTax())
                            .total(itemRequest.getTotal())
                            .description(itemRequest.getDescription())
                            .build();
                    item.prePersist();
                    purchaseOrderItemRepository.save(item);
                    savedOrder.getItems().add(item);
                }
            }

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Purchase Order"), mapPurchaseOrderToResponse(savedOrder));
        });
    }

    public CommonResponse updatePurchaseOrder(UUID purchaseOrderId, UpdatePurchaseOrderRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PurchaseOrderRepository purchaseOrderRepository = tenantTransactionalUtil.getRepository(entityManager, PurchaseOrderRepository.class);
            SupplierRepository supplierRepository = tenantTransactionalUtil.getRepository(entityManager, SupplierRepository.class);

            Optional<PurchaseOrder> orderOpt = purchaseOrderRepository.findById(purchaseOrderId);
            if (orderOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Purchase Order"));
            }
            PurchaseOrder purchaseOrder = orderOpt.get();

            if (purchaseOrderRepository.existsByOrderNumberAndPurchaseOrderIdNot(request.getOrderNumber(), purchaseOrderId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Order with this number"));
            }

            if (request.getSupplierId() != null) {
                Optional<Supplier> supplierOpt = supplierRepository.findById(request.getSupplierId());
                if (supplierOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Supplier"));
                }
                purchaseOrder.setSupplier(supplierOpt.get());
            }

            purchaseOrder.setOrderNumber(request.getOrderNumber());
            purchaseOrder.setOrderDate(request.getOrderDate());
            purchaseOrder.setExpectedDate(request.getExpectedDate());
            purchaseOrder.setSubtotal(request.getSubtotal());
            purchaseOrder.setTaxAmount(request.getTaxAmount());
            purchaseOrder.setTotal(request.getTotal());
            purchaseOrder.setStatus(request.getStatus());
            purchaseOrder.preUpdate();

            PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Purchase Order"), mapPurchaseOrderToResponse(updatedOrder));
        });
    }

    public CommonResponse getAllPurchaseOrders(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PurchaseOrderRepository purchaseOrderRepository = tenantTransactionalUtil.getRepository(entityManager, PurchaseOrderRepository.class);
            
            String sortProp = (sortBy == null || sortBy.isBlank()) ? "orderDate" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<PurchaseOrder> orderPage = purchaseOrderRepository.findAll(pageable);
            
            List<PurchaseOrderResponseDTO> content = orderPage.getContent().stream()
                    .map(this::mapPurchaseOrderToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", orderPage.getNumber());
            response.put("size", orderPage.getSize());
            response.put("totalElements", orderPage.getTotalElements());
            response.put("totalPages", orderPage.getTotalPages());
            response.put("isLast", orderPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getPurchaseOrderById(UUID purchaseOrderId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PurchaseOrderRepository purchaseOrderRepository = tenantTransactionalUtil.getRepository(entityManager, PurchaseOrderRepository.class);

            Optional<PurchaseOrder> orderOpt = purchaseOrderRepository.findById(purchaseOrderId);

            return orderOpt.map(order -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Purchase Order retrieved successfully", mapPurchaseOrderToResponse(order))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Purchase Order")));
        });
    }

    public CommonResponse deletePurchaseOrder(UUID purchaseOrderId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PurchaseOrderRepository purchaseOrderRepository = tenantTransactionalUtil.getRepository(entityManager, PurchaseOrderRepository.class);

            Optional<PurchaseOrder> orderOpt = purchaseOrderRepository.findById(purchaseOrderId);
            if (orderOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Purchase Order"));
            }

            purchaseOrderRepository.delete(orderOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Purchase Order"));
        });
    }

    private PurchaseOrderResponseDTO mapPurchaseOrderToResponse(PurchaseOrder order){
        List<PurchaseOrderItemResponseDTO> items = order.getItems().stream()
                .map(this::mapPurchaseOrderItemToResponse)
                .collect(Collectors.toList());

        return PurchaseOrderResponseDTO.builder()
                .purchaseOrderId(order.getPurchaseOrderId())
                .supplierId(order.getSupplier() != null ? order.getSupplier().getSupplierId() : null)
                .supplierName(order.getSupplier() != null ? order.getSupplier().getName() : null)
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .expectedDate(order.getExpectedDate())
                .subtotal(order.getSubtotal())
                .taxAmount(order.getTaxAmount())
                .total(order.getTotal())
                .status(order.getStatus().name())
                .items(items)
                .build();
    }

    private PurchaseOrderItemResponseDTO mapPurchaseOrderItemToResponse(PurchaseOrderItem item){
        return PurchaseOrderItemResponseDTO.builder()
                .purchaseOrderItemId(item.getPurchaseOrderItemId())
                .productId(item.getProduct() != null ? item.getProduct().getProductId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discount(item.getDiscount())
                .tax(item.getTax())
                .total(item.getTotal())
                .description(item.getDescription())
                .build();
    }
}
