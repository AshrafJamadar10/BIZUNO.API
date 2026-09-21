package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.BusinessRoleCrudPermissionResponseDTO;
import com.bizuno.dtos.business.BusinessRoleResponseDTO;
import com.bizuno.dtos.business.CreateBusinessRoleRequestDTO;
import com.bizuno.dtos.business.UpdateBusinessRoleRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.business.BusinessRole;
import com.bizuno.models.business.BusinessRoleCrudPermission;
import com.bizuno.models.business.BusinessUser;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.BusinessRoleRepository;
import com.bizuno.repositories.business.BusinessUserRepository;
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
public class BusinessRoleService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createBusinessRole(CreateBusinessRoleRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            BusinessRole businessRole = BusinessRole.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .type(request.getType())
                    .title(request.getTitle())
                    .crudPermissions(new HashSet<>())
                    .build();

            BusinessRole savedRole = businessRoleRepository.save(businessRole);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Business Role"), mapBusinessRoleToResponse(savedRole));
        });
    }

    public CommonResponse updateBusinessRole(UUID roleId, UpdateBusinessRoleRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<BusinessRole> roleOpt = businessRoleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business Role"));
            }
            BusinessRole businessRole = roleOpt.get();

            businessRole.setName(request.getName());
            businessRole.setDescription(request.getDescription());
            businessRole.setType(request.getType());
            businessRole.setTitle(request.getTitle());

            BusinessRole updatedRole = businessRoleRepository.save(businessRole);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Business Role"), mapBusinessRoleToResponse(updatedRole));
        });
    }

    public CommonResponse getAllBusinessRoles(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "name" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<BusinessRole> rolePage = businessRoleRepository.findAll(pageable);
            
            List<BusinessRoleResponseDTO> content = rolePage.getContent().stream()
                    .map(this::mapBusinessRoleToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", rolePage.getNumber());
            response.put("size", rolePage.getSize());
            response.put("totalElements", rolePage.getTotalElements());
            response.put("totalPages", rolePage.getTotalPages());
            response.put("isLast", rolePage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getBusinessRoleById(UUID roleId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<BusinessRole> roleOpt = businessRoleRepository.findById(roleId);

            return roleOpt.map(role -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Business Role retrieved successfully", mapBusinessRoleToResponse(role))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business Role")));
        });
    }

    public CommonResponse deleteBusinessRole(UUID roleId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<BusinessRole> roleOpt = businessRoleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business Role"));
            }

            businessRoleRepository.delete(roleOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Business Role"));
        });
    }

    private BusinessRoleResponseDTO mapBusinessRoleToResponse(BusinessRole role){
        Set<BusinessRoleCrudPermissionResponseDTO> permissions = role.getCrudPermissions().stream()
                .map(this::mapPermissionToResponse)
                .collect(Collectors.toSet());

        return BusinessRoleResponseDTO.builder()
                .roleId(role.getRoleId())
                .name(role.getName())
                .description(role.getDescription())
                .type(role.getType())
                .title(role.getTitle())
                .crudPermissions(permissions)
                .build();
    }

    private BusinessRoleCrudPermissionResponseDTO mapPermissionToResponse(BusinessRoleCrudPermission permission){
        return BusinessRoleCrudPermissionResponseDTO.builder()
                .permissionId(permission.getCrudRolePermissionId())
                .scope(permission.getScope())
                .operations(permission.getOperations())
                .build();
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
