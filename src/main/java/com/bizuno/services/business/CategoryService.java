package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CategoryResponseDTO;
import com.bizuno.dtos.business.CreateCategoryRequestDTO;
import com.bizuno.dtos.business.UpdateCategoryRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.Category;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.CategoryRepository;
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
public class CategoryService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createCategory(CreateCategoryRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CategoryRepository categoryRepository = tenantTransactionalUtil.getRepository(entityManager, CategoryRepository.class);

            if (categoryRepository.existsByName(request.getName())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Category"));
            }

            Category category = Category.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .build();
            category.prePersist();

            Category savedCategory = categoryRepository.save(category);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Category"), mapCategoryToResponse(savedCategory));
        });
    }

    public CommonResponse updateCategory(UUID categoryId, UpdateCategoryRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CategoryRepository categoryRepository = tenantTransactionalUtil.getRepository(entityManager, CategoryRepository.class);

            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Category"));
            }
            Category category = categoryOpt.get();

            if (categoryRepository.existsByNameAndCategoryIdNot(category.getName(), categoryId)){
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Category"));
            }

            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.preUpdate();

            Category updatedCategory = categoryRepository.save(category);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Category"), mapCategoryToResponse(updatedCategory));
        });
    }

    public CommonResponse getAllCategories(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CategoryRepository categoryRepository = tenantTransactionalUtil.getRepository(entityManager, CategoryRepository.class);
            
            String sortProp = (sortBy == null || sortBy.isBlank()) ? "name" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Category> categoryPage = categoryRepository.findAll(pageable);
            
            List<CategoryResponseDTO> content = categoryPage.getContent().stream()
                    .map(this::mapCategoryToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", categoryPage.getNumber());
            response.put("size", categoryPage.getSize());
            response.put("totalElements", categoryPage.getTotalElements());
            response.put("totalPages", categoryPage.getTotalPages());
            response.put("isLast", categoryPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getCategoryById(UUID categoryId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CategoryRepository categoryRepository = tenantTransactionalUtil.getRepository(entityManager, CategoryRepository.class);

            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

            return categoryOpt.map(category -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Category retrieved successfully", mapCategoryToResponse(category))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Category")));
        });
    }

    public CommonResponse deleteCategory(UUID categoryId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CategoryRepository categoryRepository = tenantTransactionalUtil.getRepository(entityManager, CategoryRepository.class);

            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Category"));
            }

            categoryRepository.delete(categoryOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Category"));
        });
    }

    private CategoryResponseDTO mapCategoryToResponse(Category category){
        return CategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
