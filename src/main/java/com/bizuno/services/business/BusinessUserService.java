package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.BusinessUserResponseDTO;
import com.bizuno.dtos.business.CreateBusinessUserRequestDTO;
import com.bizuno.dtos.business.UpdateBusinessUserRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.BusinessRole;
import com.bizuno.models.business.BusinessUser;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.BusinessRoleRepository;
import com.bizuno.repositories.business.BusinessUserRepository;
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
public class BusinessUserService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createBusinessUser(CreateBusinessUserRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessUserRepository businessUserRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessUserRepository.class);
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            Optional<BusinessUser> existingByPhone = businessUserRepository.findByCredential(request.getPhoneNumber());
            if (existingByPhone.isPresent()) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "User with this phone number"));
            }

            Optional<BusinessUser> existingByEmail = businessUserRepository.findByCredential(request.getEmail());
            if (existingByEmail.isPresent()) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "User with this email"));
            }

            BusinessRole role = null;
            if (request.getRoleId() != null) {
                Optional<BusinessRole> roleOpt = businessRoleRepository.findById(request.getRoleId());
                if (roleOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Role"));
                }
                role = roleOpt.get();
            }

            BusinessUser businessUser = BusinessUser.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phoneNumber(request.getPhoneNumber())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role(role)
                    .build();

            BusinessUser savedUser = businessUserRepository.save(businessUser);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Business User"), mapBusinessUserToResponse(savedUser));
        });
    }

    public CommonResponse updateBusinessUser(UUID userId, UpdateBusinessUserRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessUserRepository businessUserRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessUserRepository.class);
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            Optional<BusinessUser> userOpt = businessUserRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business User"));
            }
            BusinessUser businessUser = userOpt.get();

            Optional<BusinessUser> existingByPhone = businessUserRepository.findByCredential(request.getPhoneNumber());
            if (existingByPhone.isPresent() && !existingByPhone.get().getUserId().equals(userId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "User with this phone number"));
            }

            Optional<BusinessUser> existingByEmail = businessUserRepository.findByCredential(request.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getUserId().equals(userId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "User with this email"));
            }

            if (request.getRoleId() != null) {
                Optional<BusinessRole> roleOpt = businessRoleRepository.findById(request.getRoleId());
                if (roleOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Role"));
                }
                businessUser.setRole(roleOpt.get());
            }

            businessUser.setFirstName(request.getFirstName());
            businessUser.setLastName(request.getLastName());
            businessUser.setPhoneNumber(request.getPhoneNumber());
            businessUser.setEmail(request.getEmail());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                businessUser.setPassword(request.getPassword());
            }

            BusinessUser updatedUser = businessUserRepository.save(businessUser);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Business User"), mapBusinessUserToResponse(updatedUser));
        });
    }

    public CommonResponse getAllBusinessUsers(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessUserRepository businessUserRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessUserRepository.class);
            
            String sortProp = (sortBy == null || sortBy.isBlank()) ? "firstName" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<BusinessUser> userPage = businessUserRepository.findAll(pageable);
            
            List<BusinessUserResponseDTO> content = userPage.getContent().stream()
                    .map(this::mapBusinessUserToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", userPage.getNumber());
            response.put("size", userPage.getSize());
            response.put("totalElements", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("isLast", userPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getBusinessUserById(UUID userId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessUserRepository businessUserRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessUserRepository.class);

            Optional<BusinessUser> userOpt = businessUserRepository.findById(userId);

            return userOpt.map(user -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Business User retrieved successfully", mapBusinessUserToResponse(user))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business User")));
        });
    }

    public CommonResponse deleteBusinessUser(UUID userId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            BusinessUserRepository businessUserRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessUserRepository.class);

            Optional<BusinessUser> userOpt = businessUserRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business User"));
            }

            businessUserRepository.delete(userOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Business User"));
        });
    }

    private BusinessUserResponseDTO mapBusinessUserToResponse(BusinessUser businessUser){
        return BusinessUserResponseDTO.builder()
                .userId(businessUser.getUserId())
                .firstName(businessUser.getFirstName())
                .lastName(businessUser.getLastName())
                .phoneNumber(businessUser.getPhoneNumber())
                .email(businessUser.getEmail())
                .roleId(businessUser.getRole() != null ? businessUser.getRole().getRoleId() : null)
                .roleName(businessUser.getRole() != null ? businessUser.getRole().getName() : null)
                .build();
    }
}
