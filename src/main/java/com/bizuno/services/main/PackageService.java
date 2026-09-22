package com.bizuno.services.main;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.main.*;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.Package;
import com.bizuno.models.main.PackageFeature;
import com.bizuno.repositories.main.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;

    public CommonResponse createPackage(PackageRequestDTO packageRequest) {
        if (packageRequest == null) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, AppConstants.MESSAGE_BAD_REQUEST);
        }

        if (packageRepository.existsByNameIgnoreCase(packageRequest.getName())) {
            return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Package name"));
        }

        if (packageRequest.getBasePrice() != null && packageRequest.getBasePrice().compareTo(BigDecimal.ZERO) < 0) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, "Base price must be greater than or equal to 0");
        }

        if (packageRequest.getSetupFee() != null && packageRequest.getSetupFee().compareTo(BigDecimal.ZERO) < 0) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, "Setup fee must be greater than or equal to 0");
        }

        if (!isValidFeatureList(packageRequest.getFeatures())) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, "Invalid package feature. Use valid BizUno scopes and CRUD operations.");
        }

        Package newPackage = mapRequestToEntity(packageRequest);
        Package savedPackage = packageRepository.save(newPackage);
        return new CommonResponse(AppConstants.STATUS_CREATED, String.format(AppConstants.MESSAGE_CREATED, "Package"), mapEntityToResponse(savedPackage));
    }

    public CommonResponse updatePackage(UUID packageId, PackageRequestDTO packageRequest) {
        if (packageId == null || packageRequest == null) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, AppConstants.MESSAGE_BAD_REQUEST);
        }

        Optional<Package> existingPackage = packageRepository.findById(packageId);
        if (existingPackage.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Package"));
        }

        Package currentPackage = existingPackage.get();

        if (packageRequest.getName() != null && !packageRequest.getName().equalsIgnoreCase(currentPackage.getName())
                && packageRepository.existsByNameIgnoreCase(packageRequest.getName())) {
            return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Package name"));
        }

        if (!isValidFeatureList(packageRequest.getFeatures())) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, "Invalid package feature. Use valid BizUno scopes and CRUD operations.");
        }

        currentPackage.setName(packageRequest.getName() != null ? packageRequest.getName() : currentPackage.getName());
        currentPackage.setDescription(packageRequest.getDescription() != null ? packageRequest.getDescription() : currentPackage.getDescription());
        currentPackage.setBasePrice(packageRequest.getBasePrice() != null ? packageRequest.getBasePrice() : currentPackage.getBasePrice());
        currentPackage.setBillingPeriod(packageRequest.getBillingPeriod() != null ? packageRequest.getBillingPeriod() : currentPackage.getBillingPeriod());
        currentPackage.setPackageDays(packageRequest.getPackageDays() != null ? packageRequest.getPackageDays() : currentPackage.getPackageDays());
        currentPackage.setTrialDays(packageRequest.getTrialDays() != null ? packageRequest.getTrialDays() : currentPackage.getTrialDays());
        currentPackage.setSetupFee(packageRequest.getSetupFee() != null ? packageRequest.getSetupFee() : currentPackage.getSetupFee());
        currentPackage.setDisplayOrder(packageRequest.getDisplayOrder() != null ? packageRequest.getDisplayOrder() : currentPackage.getDisplayOrder());
        currentPackage.setRecommended(packageRequest.getRecommended() != null ? packageRequest.getRecommended() : currentPackage.getRecommended());

        if (currentPackage.getBasePrice() != null && currentPackage.getBasePrice().compareTo(BigDecimal.ZERO) < 0) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, "Base price must be greater than or equal to 0");
        }

        if (currentPackage.getSetupFee() != null && currentPackage.getSetupFee().compareTo(BigDecimal.ZERO) < 0) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, "Setup fee must be greater than or equal to 0");
        }

        if (packageRequest.getFeatures() != null) {
            currentPackage.getFeatures().clear();
            packageRequest.getFeatures().forEach(featureDto -> {
                PackageFeature feature = mapFeatureDtoToEntity(featureDto, currentPackage);
                currentPackage.getFeatures().add(feature);
            });
        }

        currentPackage.normalizePackageData();
        Package updatedPackage = packageRepository.save(currentPackage);
        return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Package"), mapEntityToResponse(updatedPackage));
    }

    public CommonResponse getAllPackages(int page, int size, String sortBy, String sortDirection) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        String sortProperty = (sortBy == null || sortBy.isBlank()) ? "displayOrder" : sortBy;
        Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<Package> packagePage = packageRepository.findAll(pageable);

        List<PackageResponseDTO> content = packagePage.getContent().stream().map(this::mapEntityToResponse).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("page", packagePage.getNumber());
        response.put("size", packagePage.getSize());
        response.put("totalElements", packagePage.getTotalElements());
        response.put("totalPages", packagePage.getTotalPages());
        response.put("isLast", packagePage.isLast());
        response.put("sortBy", sortProperty);
        response.put("sortDirection", direction.toString());

        return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
    }

    public CommonResponse getPackage(UUID packageId) {
        if (packageId == null) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, AppConstants.MESSAGE_BAD_REQUEST);
        }

        Optional<Package> packageEntity = packageRepository.findById(packageId);
        return packageEntity.map(entity ->
                new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, mapEntityToResponse(entity)))
                .orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Package")));
    }

    public CommonResponse deletePackage(UUID packageId) {
        if (packageId == null) {
            return new CommonResponse(AppConstants.STATUS_BAD_REQUEST, AppConstants.MESSAGE_BAD_REQUEST);
        }

        Optional<Package> packageEntity = packageRepository.findById(packageId);
        if (packageEntity.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Package"));
        }

        packageRepository.deleteById(packageId);
        return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Package"));
    }

    public CommonResponse getScopes() {
        return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, ModelEnums.CrudPermissionScopes.getAllValues());
    }

    private Package mapRequestToEntity(PackageRequestDTO request) {
        Package packageEntity = new Package();
        packageEntity.setName(request.getName());
        packageEntity.setDescription(request.getDescription());
        packageEntity.setBasePrice(request.getBasePrice());
        packageEntity.setBillingPeriod(request.getBillingPeriod());
        packageEntity.setPackageDays(request.getPackageDays());
        packageEntity.setTrialDays(request.getTrialDays());
        packageEntity.setSetupFee(request.getSetupFee());
        packageEntity.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        packageEntity.setRecommended(request.getRecommended() != null ? request.getRecommended() : false);

        if (request.getFeatures() != null) {
            request.getFeatures().forEach(featureDto -> {
                PackageFeature feature = mapFeatureDtoToEntity(featureDto, packageEntity);
                packageEntity.getFeatures().add(feature);
            });
        }

        packageEntity.normalizePackageData();
        return packageEntity;
    }

    private PackageFeature mapFeatureDtoToEntity(PackageFeatureRequestDTO dto, Package packageEntity) {
        PackageFeature feature = PackageFeature.builder()
                .packageFeatureCode(dto.getFeatureCode())
                .featureName(dto.getFeatureName())
                .description(dto.getDescription())
                .scope(dto.getScope().name())
                .limitType(dto.getLimitType() != null ? dto.getLimitType() : ModelEnums.FeatureLimitType.NONE)
                .limitValue(dto.getLimitValue() != null ? dto.getLimitValue() : 0)
                .unit(dto.getUnit())
                .isEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .pack(packageEntity)
                .build();

        if (dto.getOperations() != null) {
            dto.getOperations().forEach(operation -> {
                if (operation != null && !operation.trim().isEmpty()) {
                    feature.addOperation(operation.trim().toUpperCase());
                }
            });
        }

        return feature;
    }

    private boolean isValidFeatureList(List<PackageFeatureRequestDTO> features) {
        if (features == null || features.isEmpty()) {
            return true;
        }

        List<ModelEnums.CrudPermissionScopes> Scopes = List.of(ModelEnums.CrudPermissionScopes.values());

        List<String> allowedScopes = Scopes.stream().map(ModelEnums.CrudPermissionScopes::name).toList();

        Set<String> allowedOperations = Set.of("CREATE", "READ", "UPDATE", "DELETE");

        for (PackageFeatureRequestDTO feature : features) {
            if (feature == null) {
                return false;
            }

            if (feature.getScope() == null || !allowedScopes.contains(feature.getScope().name())) {
                return false;
            }

            if (feature.getOperations() == null || feature.getOperations().isEmpty()) {
                return false;
            }

            for (String operation : feature.getOperations()) {
                if (operation == null || !allowedOperations.contains(operation.trim().toUpperCase())) {
                    return false;
                }
            }

            if (feature.getLimitType() != null && feature.getLimitValue() != null && feature.getLimitValue() < 0) {
                return false;
            }
        }

        return true;
    }

    private PackageResponseDTO mapEntityToResponse(Package packageEntity) {
        if (packageEntity == null) {
            return null;
        }

        return PackageResponseDTO.builder()
                .packageId(packageEntity.getPackageId())
                .name(packageEntity.getName())
                .description(packageEntity.getDescription())
                .basePrice(packageEntity.getBasePrice())
                .billingPeriod(packageEntity.getBillingPeriod())
                .packageDays(packageEntity.getPackageDays())
                .trialDays(packageEntity.getTrialDays())
                .setupFee(packageEntity.getSetupFee())
                .displayOrder(packageEntity.getDisplayOrder())
                .recommended(packageEntity.getRecommended())
                .features(packageEntity.getFeatures().stream().map(this::mapFeatureEntityToResponse).collect(Collectors.toSet()))
                .build();
    }

    private PackageFeatureResponseDTO mapFeatureEntityToResponse(PackageFeature featureEntity) {
        if (featureEntity == null) {
            return null;
        }

        return PackageFeatureResponseDTO.builder()
                .packageFeatureId(featureEntity.getPackageFeatureId())
                .packageFeatureCode(featureEntity.getPackageFeatureCode())
                .featureName(featureEntity.getFeatureName())
                .description(featureEntity.getDescription())
                .scope(featureEntity.getScope())
                .operations(featureEntity.getOperations())
                .limitType(featureEntity.getLimitType())
                .limitValue(featureEntity.getLimitValue())
                .unit(featureEntity.getUnit())
                .isEnabled(featureEntity.getIsEnabled())
                .displayOrder(featureEntity.getDisplayOrder())
                .build();
    }
}
