package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateInventoryRequestDTO;
import com.bizuno.dtos.business.InventoryResponseDTO;
import com.bizuno.dtos.business.UpdateInventoryRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.Inventory;
import com.bizuno.models.business.Product;
import com.bizuno.models.business.Warehouse;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.CategoryRepository;
import com.bizuno.repositories.business.InventoryRepository;
import com.bizuno.repositories.business.ProductRepository;
import com.bizuno.repositories.business.WarehouseRepository;
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
public class InventoryService {

    private final TenantTransactionalUtil tenantTransactionalUtil;
    private final BusinessRepository businessRepository;

    public CommonResponse getAllInventoryMovements(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            InventoryRepository inventoryRepository = tenantTransactionalUtil.getRepository(entityManager, InventoryRepository.class);

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "createdBy" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);

            List<InventoryResponseDTO> content = inventoryPage.getContent().stream()
                    .map(this::mapInventoryToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", inventoryPage.getNumber());
            response.put("size", inventoryPage.getSize());
            response.put("totalElements", inventoryPage.getTotalElements());
            response.put("totalPages", inventoryPage.getTotalPages());
            response.put("isLast", inventoryPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getInventoryMovementById(UUID inventoryMovementId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            InventoryRepository inventoryRepository = tenantTransactionalUtil.getRepository(entityManager, InventoryRepository.class);

            Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryMovementId);

            return inventoryOpt.map(inventory -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Inventory movement retrieved successfully", mapInventoryToResponse(inventory)))
                    .orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Inventory movement")));
        });
    }

    public CommonResponse updateInventoryMovement(UUID inventoryMovementId, UpdateInventoryRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            InventoryRepository inventoryRepository = tenantTransactionalUtil.getRepository(entityManager, InventoryRepository.class);
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);
            WarehouseRepository warehouseRepository = tenantTransactionalUtil.getRepository(entityManager, WarehouseRepository.class);

            Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryMovementId);
            if (inventoryOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Inventory movement"));
            }

            Inventory inventory = inventoryOpt.get();

            Optional<Product> productOpt = productRepository.findById(request.getProductId());
            if (productOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Product"));
            }
            inventory.setProduct(productOpt.get());

            if (request.getWarehouseId() != null) {
                Optional<Warehouse> warehouseOpt = warehouseRepository.findById(request.getWarehouseId());
                if (warehouseOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Warehouse"));
                }
                inventory.setWarehouse(warehouseOpt.get());
            }

//            inventory.setMovementType(request.getMovementType());
            inventory.setQuantity(request.getQuantity());
            inventory.setNote(request.getNote());
            inventory.preUpdate();

            Inventory updatedInventory = inventoryRepository.save(inventory);
            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Inventory movement"), mapInventoryToResponse(updatedInventory));
        });
    }

    public CommonResponse deleteInventoryMovement(UUID inventoryMovementId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            InventoryRepository inventoryRepository = tenantTransactionalUtil.getRepository(entityManager, InventoryRepository.class);

            Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryMovementId);
            if (inventoryOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Inventory movement"));
            }

            inventoryRepository.delete(inventoryOpt.get());
            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Inventory movement"));
        });
    }

    public CommonResponse getInventoryByProduct(UUID productId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            InventoryRepository inventoryRepository = tenantTransactionalUtil.getRepository(entityManager, InventoryRepository.class);
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);

            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Product"));
            }

            List<Inventory> inventoryList = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getProduct().getProductId().equals(productId))
                    .toList();

            List<InventoryResponseDTO> content = inventoryList.stream()
                    .map(this::mapInventoryToResponse)
                    .collect(Collectors.toList());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, "Inventory movements retrieved successfully", content);
        });
    }

    public CommonResponse getInventoryByWarehouse(UUID warehouseId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            InventoryRepository inventoryRepository = tenantTransactionalUtil.getRepository(entityManager, InventoryRepository.class);
            WarehouseRepository warehouseRepository = tenantTransactionalUtil.getRepository(entityManager, WarehouseRepository.class);

            Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
            if (warehouseOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Warehouse"));
            }

            List<Inventory> inventoryList = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getWarehouse() != null && inv.getWarehouse().getWarehouseId().equals(warehouseId))
                    .toList();

            List<InventoryResponseDTO> content = inventoryList.stream()
                    .map(this::mapInventoryToResponse)
                    .collect(Collectors.toList());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, "Inventory movements retrieved successfully", content);
        });
    }

    private InventoryResponseDTO mapInventoryToResponse(Inventory inventory) {
        return InventoryResponseDTO.builder()
                .inventoryMovementId(inventory.getInventoryMovementId())
                .productId(inventory.getProduct() != null ? inventory.getProduct().getProductId() : null)
                .productName(inventory.getProduct() != null ? inventory.getProduct().getName() : null)
                .productSku(inventory.getProduct() != null ? inventory.getProduct().getSku() : null)
                .warehouseId(inventory.getWarehouse() != null ? inventory.getWarehouse().getWarehouseId() : null)
                .warehouseName(inventory.getWarehouse() != null ? inventory.getWarehouse().getName() : null)
//                .movementType(inventory.getMovementType())
                .quantity(inventory.getQuantity())
                .note(inventory.getNote())
                .createdAt(inventory.getCreatedDate())
                .updatedAt(inventory.getModifiedDate())
                .createdBy(inventory.getCreatedBy())
                .updatedBy(inventory.getModifiedBy())
                .build();
    }
}
