package com.bizuno.services.main;

import com.bizuno.config.BusinessDatabaseConfig;
import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.main.CreateBusinessRequestDTO;
import com.bizuno.dtos.main.BusinessResponseDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.utils.Codes;
import com.bizuno.utils.UniqueCodeGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final DatabaseCreationService databaseCreationService;
    private final BusinessDatabaseConfig businessDatabaseConfig;
    private final Codes codes;

    @Value("${packages-to-scan.business}")
    private String tenantPackagesToScan;

    public CommonResponse createBusiness(CreateBusinessRequestDTO createBusinessRequestDTO) {
        System.out.println("🚀 Starting tenant creation for: " + createBusinessRequestDTO.getBusinessName());
        
        Business business = new Business();
        business.setBusinessName(createBusinessRequestDTO.getBusinessName());
        business.setEmail(createBusinessRequestDTO.getEmail());
        business.setPhone(createBusinessRequestDTO.getPhone());
        business.prePersist();

        String tenantId = UUID.randomUUID().toString();
        while (businessRepository.existsByTenantId(tenantId)){
            tenantId = UUID.randomUUID().toString();
        }
        business.setTenantId(tenantId);

        String businessCode = codes.generateBusinessCode(business.getBusinessName());
        while (businessRepository.existsByBusinessCode(businessCode)){
            businessCode = codes.generateBusinessCode(business.getBusinessName());
        }
        business.setBusinessCode(businessCode);

        String dbName = getDatabaseName(createBusinessRequestDTO.getBusinessName());
        while (databaseCreationService.isDatabaseExists(dbName)) {
            dbName = getDatabaseName(createBusinessRequestDTO.getBusinessName());
        }
        business.setDbName(dbName);

        Business savedBusiness = businessRepository.save(business);
        System.out.println("✅ Tenant saved with tenant ID: " + savedBusiness.getTenantId());

        databaseCreationService.createTenantDatabase(dbName);
        System.out.println("✅ Database created: " + dbName);

        System.out.println("🔧 Creating EntityManagerFactory for tenant ID: " + business.getTenantId());
        businessDatabaseConfig.getOrCreateTenantEntityManagerFactory(
            business.getTenantId(), dbName, tenantPackagesToScan
        );
        System.out.println("✅ EntityManagerFactory creation completed");

        return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "business"), getBusinessResponseDTO(savedBusiness));
    }

    public CommonResponse updateBusiness(UUID businessId, @Valid CreateBusinessRequestDTO createBusinessRequestDTO) {
        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
        if (optionalBusiness.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "business"));
        }
        Business business = optionalBusiness.get();

        business.setBusinessName(createBusinessRequestDTO.getBusinessName());
        business.setEmail(createBusinessRequestDTO.getEmail());
        business.setPhone(createBusinessRequestDTO.getPhone());
        business.preUpdate();

        businessRepository.save(business);

        return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "business"), getBusinessResponseDTO(business));
    }

    public CommonResponse getAllBusiness(int page, int size, String sortBy, String sortDirection) {
            if (page < 0) page = 0;
            if (size <= 0) size = 10;

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "businessName" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Business> businessPage = businessRepository.findAll(pageable);

            List<BusinessResponseDTO> content = businessPage.getContent().stream().map(this::getBusinessResponseDTO).toList();

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", businessPage.getNumber());
            response.put("size", businessPage.getSize());
            response.put("totalElements", businessPage.getTotalElements());
            response.put("totalPages", businessPage.getTotalPages());
            response.put("isLast", businessPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        }

    public CommonResponse deleteBusiness(UUID businessId) {
        Optional<Business> business = businessRepository.findById(businessId);

        if (business.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "business"));
        }

        businessDatabaseConfig.closeTenantEntityManagerFactory(businessId.toString());
        databaseCreationService.dropTenantDatabase(business.get().getDbName());
        businessRepository.deleteById(businessId);

        return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "business"));
    }

    public CommonResponse getBusiness(UUID businessId) {
        Optional<Business> business = businessRepository.findById(businessId);

        return business.map(value ->
                new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, getBusinessResponseDTO(value)))
                .orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "business")));
    }

    public static String getDatabaseName(String input) {
        String[] words = input.trim().split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z0-9]", "");

            if (word.equalsIgnoreCase("private")) {
                word = "Pvt";
            } else if (word.equalsIgnoreCase("limited")) {
                word = "Ltd";
            } else if (!word.isEmpty()) {
                word = Character.toUpperCase(word.charAt(0))
                        + word.substring(1).toLowerCase();
            }

            result.append(word);
        }

        return Character.toLowerCase(result.charAt(0)) + result.substring(1) + UniqueCodeGenerator.generateShortUuid();
    }

    private BusinessResponseDTO getBusinessResponseDTO(Business business){
        return BusinessResponseDTO.builder()
                .businessId(business.getBusinessId())
                .businessName(business.getBusinessName())
                .businessCode(business.getBusinessCode())
                .email(business.getEmail())
                .phone(business.getPhone())
                .logo(business.getLogo())
                .createdDate(business.getCreatedDate())
                .isActive(business.getIsActive())
//                .plan(business.getPlan())
//                .ownerName(business.getOwnerName())
                .build();
    }
}
