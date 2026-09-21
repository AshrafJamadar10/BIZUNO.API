package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateStaffAttendanceRequestDTO;
import com.bizuno.dtos.business.StaffAttendanceResponseDTO;
import com.bizuno.dtos.business.UpdateStaffAttendanceRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.business.BusinessUser;
import com.bizuno.models.business.Staff;
import com.bizuno.models.business.StaffAttendance;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.BusinessUserRepository;
import com.bizuno.repositories.business.StaffAttendanceRepository;
import com.bizuno.repositories.business.StaffRepository;
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
public class StaffAttendanceService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createStaffAttendance(CreateStaffAttendanceRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffAttendanceRepository staffAttendanceRepository = tenantTransactionalUtil.getRepository(entityManager, StaffAttendanceRepository.class);
            StaffRepository staffRepository = tenantTransactionalUtil.getRepository(entityManager, StaffRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Staff> staffOpt = staffRepository.findById(request.getStaffId());
            if (staffOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Staff"));
            }

            if (staffAttendanceRepository.existsByStaffIdAndAttendanceDate(request.getStaffId(), request.getAttendanceDate())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Attendance for this staff on this date"));
            }

            StaffAttendance attendance = StaffAttendance.builder()
                    .staff(staffOpt.get())
                    .attendanceDate(request.getAttendanceDate())
                    .status(request.getStatus())
                    .checkIn(request.getCheckIn())
                    .checkOut(request.getCheckOut())
                    .note(request.getNote())
                    .build();
            attendance.prePersist();

            StaffAttendance savedAttendance = staffAttendanceRepository.save(attendance);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Staff Attendance"), mapStaffAttendanceToResponse(savedAttendance));
        });
    }

    public CommonResponse updateStaffAttendance(UUID staffAttendanceId, UpdateStaffAttendanceRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffAttendanceRepository staffAttendanceRepository = tenantTransactionalUtil.getRepository(entityManager, StaffAttendanceRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<StaffAttendance> attendanceOpt = staffAttendanceRepository.findById(staffAttendanceId);
            if (attendanceOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Staff Attendance"));
            }
            StaffAttendance attendance = attendanceOpt.get();

            attendance.setAttendanceDate(request.getAttendanceDate());
            attendance.setStatus(request.getStatus());
            attendance.setCheckIn(request.getCheckIn());
            attendance.setCheckOut(request.getCheckOut());
            attendance.setNote(request.getNote());
            attendance.preUpdate();

            StaffAttendance updatedAttendance = staffAttendanceRepository.save(attendance);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Staff Attendance"), mapStaffAttendanceToResponse(updatedAttendance));
        });
    }

    public CommonResponse getAllStaffAttendance(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffAttendanceRepository staffAttendanceRepository = tenantTransactionalUtil.getRepository(entityManager, StaffAttendanceRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "attendanceDate" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<StaffAttendance> attendancePage = staffAttendanceRepository.findAll(pageable);
            
            List<StaffAttendanceResponseDTO> content = attendancePage.getContent().stream()
                    .map(this::mapStaffAttendanceToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", attendancePage.getNumber());
            response.put("size", attendancePage.getSize());
            response.put("totalElements", attendancePage.getTotalElements());
            response.put("totalPages", attendancePage.getTotalPages());
            response.put("isLast", attendancePage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getStaffAttendanceById(UUID staffAttendanceId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffAttendanceRepository staffAttendanceRepository = tenantTransactionalUtil.getRepository(entityManager, StaffAttendanceRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<StaffAttendance> attendanceOpt = staffAttendanceRepository.findById(staffAttendanceId);

            return attendanceOpt.map(attendance -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Staff Attendance retrieved successfully", mapStaffAttendanceToResponse(attendance))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Staff Attendance")));
        });
    }

    public CommonResponse deleteStaffAttendance(UUID staffAttendanceId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            StaffAttendanceRepository staffAttendanceRepository = tenantTransactionalUtil.getRepository(entityManager, StaffAttendanceRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<StaffAttendance> attendanceOpt = staffAttendanceRepository.findById(staffAttendanceId);
            if (attendanceOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Staff Attendance"));
            }

            staffAttendanceRepository.delete(attendanceOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Staff Attendance"));
        });
    }

    private StaffAttendanceResponseDTO mapStaffAttendanceToResponse(StaffAttendance attendance){
        return StaffAttendanceResponseDTO.builder()
                .staffAttendanceId(attendance.getStaffAttendanceId())
                .staffId(attendance.getStaff() != null ? attendance.getStaff().getStaffId() : null)
                .staffName(attendance.getStaff() != null ? attendance.getStaff().getFullName() : null)
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus().name())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .note(attendance.getNote())
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
