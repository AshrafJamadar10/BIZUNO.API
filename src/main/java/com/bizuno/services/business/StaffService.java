package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateStaffRequestDTO;
import com.bizuno.dtos.business.StaffResponseDTO;
import com.bizuno.dtos.business.UpdateStaffRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.BusinessRole;
import com.bizuno.models.business.Staff;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.BusinessRoleRepository;
import com.bizuno.repositories.business.StaffRepository;
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
public class StaffService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createStaff(CreateStaffRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffRepository staffRepository = tenantTransactionalUtil.getRepository(entityManager, StaffRepository.class);
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            if (staffRepository.existsByEmail(request.getEmail())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Staff with this email"));
            }
            if (staffRepository.existsByPhone(request.getPhone())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Staff with this phone"));
            }

            Optional<BusinessRole> roleOpt = businessRoleRepository.findById(request.getRoleId());
            if (roleOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Role"));
            }

            Staff staff = Staff.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .department(request.getDepartment())
                    .designation(request.getDesignation())
                    .role(roleOpt.get())
                    .build();
            staff.prePersist();

            Staff savedStaff = staffRepository.save(staff);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Staff"), mapStaffToResponse(savedStaff));
        });
    }

    public CommonResponse updateStaff(UUID staffId, UpdateStaffRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffRepository staffRepository = tenantTransactionalUtil.getRepository(entityManager, StaffRepository.class);
            BusinessRoleRepository businessRoleRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessRoleRepository.class);

            Optional<Staff> staffOpt = staffRepository.findById(staffId);
            if (staffOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Staff"));
            }
            Staff staff = staffOpt.get();

            if (staffRepository.existsByEmailAndStaffIdNot(request.getEmail(), staffId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Staff with this email"));
            }
            if (staffRepository.existsByPhoneAndStaffIdNot(request.getPhone(), staffId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Staff with this phone"));
            }

            if (request.getRoleId() != null) {
                Optional<BusinessRole> roleOpt = businessRoleRepository.findById(request.getRoleId());
                if (roleOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Role"));
                }
                staff.setRole(roleOpt.get());
            }

            staff.setFullName(request.getFullName());
            staff.setEmail(request.getEmail());
            staff.setPhone(request.getPhone());
            staff.setDepartment(request.getDepartment());
            staff.setDesignation(request.getDesignation());
            staff.preUpdate();

            Staff updatedStaff = staffRepository.save(staff);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Staff"), mapStaffToResponse(updatedStaff));
        });
    }

    public CommonResponse getAllStaff(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffRepository staffRepository = tenantTransactionalUtil.getRepository(entityManager, StaffRepository.class);
            
            String sortProp = (sortBy == null || sortBy.isBlank()) ? "fullName" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Staff> staffPage = staffRepository.findAll(pageable);
            
            List<StaffResponseDTO> content = staffPage.getContent().stream()
                    .map(this::mapStaffToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", staffPage.getNumber());
            response.put("size", staffPage.getSize());
            response.put("totalElements", staffPage.getTotalElements());
            response.put("totalPages", staffPage.getTotalPages());
            response.put("isLast", staffPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getStaffById(UUID staffId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffRepository staffRepository = tenantTransactionalUtil.getRepository(entityManager, StaffRepository.class);

            Optional<Staff> staffOpt = staffRepository.findById(staffId);

            return staffOpt.map(staff -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Staff retrieved successfully", mapStaffToResponse(staff))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Staff")));
        });
    }

    public CommonResponse deleteStaff(UUID staffId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffRepository staffRepository = tenantTransactionalUtil.getRepository(entityManager, StaffRepository.class);

            Optional<Staff> staffOpt = staffRepository.findById(staffId);
            if (staffOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Staff"));
            }

            staffRepository.delete(staffOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Staff"));
        });
    }

    private StaffResponseDTO mapStaffToResponse(Staff staff){
        return StaffResponseDTO.builder()
                .staffId(staff.getStaffId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .phone(staff.getPhone())
                .department(staff.getDepartment())
                .designation(staff.getDesignation())
                .status(staff.getStatus().name())
                .roleId(staff.getRole() != null ? staff.getRole().getRoleId() : null)
                .roleName(staff.getRole() != null ? staff.getRole().getName() : null)
                .build();
    }
}
