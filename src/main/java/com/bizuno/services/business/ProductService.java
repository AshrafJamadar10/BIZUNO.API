package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateProductRequestDTO;
import com.bizuno.dtos.business.ProductResponseDTO;
import com.bizuno.dtos.business.UpdateProductRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.business.*;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.*;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.utils.TenantTransactionalUtil;
import jakarta.persistence.EntityManager;
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
public class ProductService {

    private final TenantTransactionalUtil tenantTransactionalUtil;
    private final BusinessRepository businessRepository;

    public CommonResponse createProduct(CreateProductRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);
            CategoryRepository categoryRepository = tenantTransactionalUtil.getRepository(entityManager, CategoryRepository.class);
            InventoryRepository inventoryRepository = tenantTransactionalUtil.getRepository(entityManager, InventoryRepository.class);
            WarehouseRepository warehouseRepository = tenantTransactionalUtil.getRepository(entityManager, WarehouseRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            if (productRepository.existsByName(request.getName())){
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Product name"));
            }

            Category category = null;
            if (request.getCategoryId() != null) {
                Optional<Category> categoryOpt = categoryRepository.findById(request.getCategoryId());
                if (categoryOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Category"));
                }
                category = categoryOpt.get();
            }

            Warehouse warehouse = null;
            if (request.getWarehouseId() != null){
                Optional<Warehouse> warehouseOpt = warehouseRepository.findById(request.getWarehouseId());
                if (warehouseOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Warehouse"));
                }
                warehouse = warehouseOpt.get();
            }

            Product product = Product.builder()
                    .category(category)
                    .name(request.getName())
                    .sku(generateSku(request.getName(), category != null ? category.getName() : ""))
                    .barcode(request.getBarcode())
                    .description(request.getDescription())
                    .unit(request.getUnit())
                    .purchasePrice(request.getPurchasePrice())
                    .sellingPrice(request.getSellingPrice())
                    .taxRate(request.getTaxRate())
                    .minStock(request.getMinStock())
                    .status(request.getStatus())
                    .build();
            product.prePersist();

            Product savedProduct = productRepository.save(product);

            Inventory inventory = Inventory.builder()
                    .product(savedProduct)
                    .quantity(0)
                    .warehouse(warehouse)
                    .note(request.getDescription())
                    .build();
            inventory.prePersist();
            inventoryRepository.save(inventory);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Product"), mapProductToResponse(savedProduct));
        });
    }

    public CommonResponse getAllProducts(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "name" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Product> productPage = productRepository.findAll(pageable);

            List<ProductResponseDTO> content = productPage.getContent().stream()
                    .map(this::mapProductToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", productPage.getNumber());
            response.put("size", productPage.getSize());
            response.put("totalElements", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());
            response.put("isLast", productPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getProductById(UUID productId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Product> productOpt = productRepository.findById(productId);

            return productOpt.map(product -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Product retrieved successfully", mapProductToResponse(product)))
                    .orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Product")));
        });
    }

    public CommonResponse updateProduct(UUID productId, UpdateProductRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);
            CategoryRepository categoryRepository = tenantTransactionalUtil.getRepository(entityManager, CategoryRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Product"));
            }

            Product product = productOpt.get();

            if (productRepository.existsByNameAndProductIdNot(request.getName(), productId)){
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Product name"));
            }

            if (request.getCategoryId() != null) {
                Optional<Category> categoryOpt = categoryRepository.findById(request.getCategoryId());
                if (categoryOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Category"));
                }
                if (!product.getCategory().equals(categoryOpt.get())){
                    product.setSku(generateSku(request.getName(), categoryOpt.get().getName()));
                }
                product.setCategory(categoryOpt.get());
            }

            product.setName(request.getName());
            product.setBarcode(request.getBarcode());
            product.setDescription(request.getDescription());
            product.setUnit(request.getUnit());
            product.setPurchasePrice(request.getPurchasePrice());
            product.setSellingPrice(request.getSellingPrice());
            product.setTaxRate(request.getTaxRate());
            product.setMinStock(request.getMinStock());
            product.setStatus(request.getStatus());
            product.preUpdate();

            Product updatedProduct = productRepository.save(product);
            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Product"), mapProductToResponse(updatedProduct));
        });
    }

    public CommonResponse deleteProduct(UUID productId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Product"));
            }

            productRepository.delete(productOpt.get());
            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Product"));
        });
    }

    private ProductResponseDTO mapProductToResponse(Product product) {
        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .name(product.getName())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .description(product.getDescription())
                .unit(product.getUnit())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .taxRate(product.getTaxRate())
                .minStock(product.getMinStock())
                .status(product.getStatus())
                .build();
    }

    private String generateSku(String productName, String category) {
        if (category.isEmpty()){
            category = UUID.randomUUID().toString().substring(0, 3).toUpperCase();
        }
        return productName.substring(0, 3).toUpperCase() +"-"+ category.substring(0, 3).toUpperCase() +"-"+ UUID.randomUUID().toString().substring(0, 3).toUpperCase();
    }

    private CommonResponse validateUser(UserDO userDO, EntityManager entityManager){

        BusinessUserRepository userRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessUserRepository.class);

        if(userDO == null){
            return new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_UNAUTHORIZED);
        }
        if (userDO.getUserType().equals(ModelEnums.RoleType.PLATFORM.name())){
            return new CommonResponse(AppConstants.STATUS_FORBIDDEN, AppConstants.MESSAGE_FORBIDDEN);
        }

        Optional<BusinessUser> userOpt = userRepository.findById(userDO.getUserId());
        return userOpt.map(businessUser -> new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, businessUser)).orElseGet(() -> new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_UNAUTHORIZED));

    }
}
