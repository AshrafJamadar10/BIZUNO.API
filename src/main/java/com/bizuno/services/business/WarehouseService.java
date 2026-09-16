package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateWarehouseRequestDTO;
import com.bizuno.dtos.business.UpdateWarehouseRequestDTO;
import com.bizuno.dtos.business.WarehouseResponseDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.Warehouse;
import com.bizuno.models.main.Business;
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
public class WarehouseService {

    private final TenantTransactionalUtil tenantTransactionalUtil;
    private final BusinessRepository businessRepository;

    public CommonResponse createWarehouse(CreateWarehouseRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            WarehouseRepository warehouseRepository = tenantTransactionalUtil.getRepository(entityManager, WarehouseRepository.class);

            if (warehouseRepository.existsByName(request.getName())){
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Warehouse name"));
            }

            Warehouse warehouse = Warehouse.builder()
                    .name(request.getName())
                    .location(request.getLocation())
                    .contactPerson(request.getContactPerson())
                    .phone(request.getPhone())
                    .build();
            warehouse.prePersist();

            Warehouse savedWarehouse = warehouseRepository.save(warehouse);
            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Warehouse"), mapWarehouseToResponse(savedWarehouse));
        });
    }

    public CommonResponse getAllWarehouses(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            WarehouseRepository warehouseRepository = tenantTransactionalUtil.getRepository(entityManager, WarehouseRepository.class);

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "name" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);

            List<WarehouseResponseDTO> content = warehousePage.getContent().stream()
                    .map(this::mapWarehouseToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", warehousePage.getNumber());
            response.put("size", warehousePage.getSize());
            response.put("totalElements", warehousePage.getTotalElements());
            response.put("totalPages", warehousePage.getTotalPages());
            response.put("isLast", warehousePage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getWarehouseById(UUID warehouseId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            WarehouseRepository warehouseRepository = tenantTransactionalUtil.getRepository(entityManager, WarehouseRepository.class);

            Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);

            return warehouseOpt.map(warehouse -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Warehouse retrieved successfully", mapWarehouseToResponse(warehouse)))
                    .orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Warehouse")));
        });
    }

    public CommonResponse updateWarehouse(UUID warehouseId, UpdateWarehouseRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            WarehouseRepository warehouseRepository = tenantTransactionalUtil.getRepository(entityManager, WarehouseRepository.class);

            Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
            if (warehouseOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Warehouse"));
            }

            if (warehouseRepository.existsByNameAndWarehouseIdNot(request.getName(), warehouseId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Warehouse name"));
            }

            Warehouse warehouse = warehouseOpt.get();
            warehouse.setName(request.getName());
            warehouse.setLocation(request.getLocation());
            warehouse.setContactPerson(request.getContactPerson());
            warehouse.setPhone(request.getPhone());
            warehouse.setStatus(request.getStatus());
            warehouse.preUpdate();

            Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Warehouse"), mapWarehouseToResponse(updatedWarehouse));
        });
    }

    public CommonResponse deleteWarehouse(UUID warehouseId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            WarehouseRepository warehouseRepository = tenantTransactionalUtil.getRepository(entityManager, WarehouseRepository.class);

            Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
            if (warehouseOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Warehouse"));
            }

            warehouseRepository.delete(warehouseOpt.get());
            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Warehouse"));
        });
    }

    private WarehouseResponseDTO mapWarehouseToResponse(Warehouse warehouse) {
        return WarehouseResponseDTO.builder()
                .warehouseId(warehouse.getWarehouseId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .contactPerson(warehouse.getContactPerson())
                .phone(warehouse.getPhone())
                .status(warehouse.getStatus())
                .build();
    }
}
