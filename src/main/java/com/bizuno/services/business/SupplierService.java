package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateSupplierRequestDTO;
import com.bizuno.dtos.business.SupplierResponseDTO;
import com.bizuno.dtos.business.UpdateSupplierRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.Supplier;
import com.bizuno.models.main.Business;
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
public class SupplierService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createSupplier(CreateSupplierRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SupplierRepository supplierRepository = tenantTransactionalUtil.getRepository(entityManager, SupplierRepository.class);

            if (supplierRepository.existsByName(request.getName())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Supplier with this name"));
            }
            if (request.getEmail() != null && supplierRepository.existsByEmail(request.getEmail())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Supplier with this email"));
            }
            if (request.getPhone() != null && supplierRepository.existsByPhone(request.getPhone())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Supplier with this phone"));
            }

            Supplier supplier = Supplier.builder()
                    .name(request.getName())
                    .contactPerson(request.getContactPerson())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .city(request.getCity())
                    .address(request.getAddress())
                    .gstNumber(request.getGstNumber())
                    .build();
            supplier.prePersist();

            Supplier savedSupplier = supplierRepository.save(supplier);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Supplier"), mapSupplierToResponse(savedSupplier));
        });
    }

    public CommonResponse updateSupplier(UUID supplierId, UpdateSupplierRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SupplierRepository supplierRepository = tenantTransactionalUtil.getRepository(entityManager, SupplierRepository.class);

            Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
            if (supplierOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Supplier"));
            }
            Supplier supplier = supplierOpt.get();

            if (supplierRepository.existsByNameAndSupplierIdNot(request.getName(), supplierId)){
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Supplier with this name"));
            }
            if (request.getEmail() != null && supplierRepository.existsByEmailAndSupplierIdNot(request.getEmail(), supplierId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Supplier with this email"));
            }
            if (request.getPhone() != null && supplierRepository.existsByPhoneAndSupplierIdNot(request.getPhone(), supplierId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Supplier with this phone"));
            }

            supplier.setName(request.getName());
            supplier.setContactPerson(request.getContactPerson());
            supplier.setEmail(request.getEmail());
            supplier.setPhone(request.getPhone());
            supplier.setCity(request.getCity());
            supplier.setAddress(request.getAddress());
            supplier.setGstNumber(request.getGstNumber());
            supplier.preUpdate();

            Supplier updatedSupplier = supplierRepository.save(supplier);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Supplier"), mapSupplierToResponse(updatedSupplier));
        });
    }

    public CommonResponse getAllSuppliers(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SupplierRepository supplierRepository = tenantTransactionalUtil.getRepository(entityManager, SupplierRepository.class);
            
            String sortProp = (sortBy == null || sortBy.isBlank()) ? "name" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Supplier> supplierPage = supplierRepository.findAll(pageable);
            
            List<SupplierResponseDTO> content = supplierPage.getContent().stream()
                    .map(this::mapSupplierToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", supplierPage.getNumber());
            response.put("size", supplierPage.getSize());
            response.put("totalElements", supplierPage.getTotalElements());
            response.put("totalPages", supplierPage.getTotalPages());
            response.put("isLast", supplierPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getSupplierById(UUID supplierId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SupplierRepository supplierRepository = tenantTransactionalUtil.getRepository(entityManager, SupplierRepository.class);

            Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);

            return supplierOpt.map(supplier -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Supplier retrieved successfully", mapSupplierToResponse(supplier))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Supplier")));
        });
    }

    public CommonResponse deleteSupplier(UUID supplierId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SupplierRepository supplierRepository = tenantTransactionalUtil.getRepository(entityManager, SupplierRepository.class);

            Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
            if (supplierOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Supplier"));
            }

            supplierRepository.delete(supplierOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Supplier"));
        });
    }

    private SupplierResponseDTO mapSupplierToResponse(Supplier supplier){
        return SupplierResponseDTO.builder()
                .supplierId(supplier.getSupplierId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .city(supplier.getCity())
                .address(supplier.getAddress())
                .gstNumber(supplier.getGstNumber())
                .outstandingBalance(supplier.getOutstandingBalance())
                .status(supplier.getStatus().name())
                .build();
    }
}
