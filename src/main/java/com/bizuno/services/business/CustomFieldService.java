package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CustomFieldResponseDTO;
import com.bizuno.dtos.business.CreateCustomFieldRequestDTO;
import com.bizuno.dtos.business.UpdateCustomFieldRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.business.BusinessUser;
import com.bizuno.models.business.CustomField;
import com.bizuno.models.business.CustomFieldValue;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.BusinessUserRepository;
import com.bizuno.repositories.business.CustomFieldRepository;
import com.bizuno.repositories.business.ProductCustomFieldValueRepository;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.utils.TenantTransactionalUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomFieldService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;
    private final ExpressionParser parser = new SpelExpressionParser();

    public CommonResponse createCustomField(CreateCustomFieldRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomFieldRepository customFieldRepository = tenantTransactionalUtil.getRepository(entityManager, CustomFieldRepository.class);

            CommonResponse response = validateUser(userDO, entityManager);
            if (response.getStatus() != AppConstants.STATUS_SUCCESS) {
                return response;
            }

            if (customFieldRepository.existsByFieldKey(request.getFieldKey())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Field key"));
            }

            CustomField customField = CustomField.builder()
                    .label(request.getLabel())
                    .fieldKey(request.getFieldKey())
                    .placeholder(request.getPlaceholder())
                    .helperText(request.getHelperText())
                    .required(request.getRequired())
                    .dataType(request.getDataType())
                    .options(request.getOptions())
                    .defaultValue(request.getDefaultValue())
                    .textColor(request.getTextColor())
                    .fieldBackgroundColor(request.getFieldBackgroundColor())
                    .borderColor(request.getBorderColor())
                    .borderRadius(request.getBorderRadius())
                    .fontWeight(request.getFontWeight())
                    .status(request.getStatus())
                    .fieldType(request.getCustomFieldType())
                    .formula(request.getFormula())
                    .dependentFieldKeys(request.getDependentFieldKeys() != null ? request.getDependentFieldKeys() : new HashSet<>())
                    .build();
            customField.prePersist();

            CustomField savedCustomField = customFieldRepository.save(customField);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Custom field"), mapCustomFieldToResponse(savedCustomField));
        });
    }

    public CommonResponse updateCustomField(UUID fieldId, UpdateCustomFieldRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomFieldRepository customFieldRepository = tenantTransactionalUtil.getRepository(entityManager, CustomFieldRepository.class);

            CommonResponse response = validateUser(userDO, entityManager);
            if (response.getStatus() != AppConstants.STATUS_SUCCESS) {
                return response;
            }

            Optional<CustomField> customFieldOpt = customFieldRepository.findById(fieldId);
            if (customFieldOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Custom field"));
            }
            CustomField customField = customFieldOpt.get();

            if (request.getFieldKey() != null && !request.getFieldKey().equals(customField.getFieldKey())) {
                if (customFieldRepository.existsByFieldKeyAndFieldIdNot(request.getFieldKey(), fieldId)) {
                    return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Field key"));
                }
                customField.setFieldKey(request.getFieldKey());
            }

            if (request.getLabel() != null) customField.setLabel(request.getLabel());
            if (request.getPlaceholder() != null) customField.setPlaceholder(request.getPlaceholder());
            if (request.getHelperText() != null) customField.setHelperText(request.getHelperText());
            if (request.getRequired() != null) customField.setRequired(request.getRequired());
            if (request.getDataType() != null) customField.setDataType(request.getDataType());
            if (request.getOptions() != null) customField.setOptions(request.getOptions());
            if (request.getDefaultValue() != null) customField.setDefaultValue(request.getDefaultValue());
            if (request.getTextColor() != null) customField.setTextColor(request.getTextColor());
            if (request.getFieldBackgroundColor() != null) customField.setFieldBackgroundColor(request.getFieldBackgroundColor());
            if (request.getBorderColor() != null) customField.setBorderColor(request.getBorderColor());
            if (request.getBorderRadius() != null) customField.setBorderRadius(request.getBorderRadius());
            if (request.getFontWeight() != null) customField.setFontWeight(request.getFontWeight());
            if (request.getStatus() != null) customField.setStatus(request.getStatus());
            if (request.getFieldType() != null) customField.setFieldType(request.getFieldType());
            if (request.getFormula() != null) customField.setFormula(request.getFormula());
            if (request.getDependentFieldKeys() != null) customField.setDependentFieldKeys(request.getDependentFieldKeys());

            customField.preUpdate();

            CustomField updatedCustomField = customFieldRepository.save(customField);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Custom field"), mapCustomFieldToResponse(updatedCustomField));
        });
    }

    public CommonResponse getAllCustomFields(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomFieldRepository customFieldRepository = tenantTransactionalUtil.getRepository(entityManager, CustomFieldRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "label" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<CustomField> customFieldPage = customFieldRepository.findAll(pageable);

            List<CustomFieldResponseDTO> content = customFieldPage.getContent().stream()
                    .map(this::mapCustomFieldToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", customFieldPage.getNumber());
            response.put("size", customFieldPage.getSize());
            response.put("totalElements", customFieldPage.getTotalElements());
            response.put("totalPages", customFieldPage.getTotalPages());
            response.put("isLast", customFieldPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getCustomFieldById(UUID fieldId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomFieldRepository customFieldRepository = tenantTransactionalUtil.getRepository(entityManager, CustomFieldRepository.class);

            CommonResponse response = validateUser(userDO, entityManager);
            if (response.getStatus() != AppConstants.STATUS_SUCCESS) {
                return response;
            }

            Optional<CustomField> customFieldOpt = customFieldRepository.findById(fieldId);

            return customFieldOpt.map(customField -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Custom field retrieved successfully", mapCustomFieldToResponse(customField)))
                    .orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Custom field")));
        });
    }

    public CommonResponse getActiveCustomFields(String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomFieldRepository customFieldRepository = tenantTransactionalUtil.getRepository(entityManager, CustomFieldRepository.class);

            CommonResponse response = validateUser(userDO, entityManager);
            if (response.getStatus() != AppConstants.STATUS_SUCCESS) {
                return response;
            }

            List<CustomField> activeFields = customFieldRepository.findByStatus(ModelEnums.ProductStatus.ACTIVE);
            List<CustomFieldResponseDTO> content = activeFields.stream()
                    .map(this::mapCustomFieldToResponse)
                    .collect(Collectors.toList());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, "Active custom fields retrieved successfully", content);
        });
    }

    public CommonResponse deleteCustomField(UUID fieldId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomFieldRepository customFieldRepository = tenantTransactionalUtil.getRepository(entityManager, CustomFieldRepository.class);

            Optional<CustomField> customFieldOpt = customFieldRepository.findById(fieldId);
            if (customFieldOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Custom field"));
            }

            customFieldRepository.delete(customFieldOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Custom field"));
        });
    }

    private CustomFieldResponseDTO mapCustomFieldToResponse(CustomField customField) {
        return CustomFieldResponseDTO.builder()
                .fieldId(customField.getFieldId())
                .label(customField.getLabel())
                .fieldKey(customField.getFieldKey())
                .placeholder(customField.getPlaceholder())
                .helperText(customField.getHelperText())
                .required(customField.getRequired())
                .dataType(customField.getDataType())
                .options(customField.getOptions())
                .defaultValue(customField.getDefaultValue())
                .textColor(customField.getTextColor())
                .fieldBackgroundColor(customField.getFieldBackgroundColor())
                .borderColor(customField.getBorderColor())
                .borderRadius(customField.getBorderRadius())
                .fontWeight(customField.getFontWeight())
                .status(customField.getStatus())
                .fieldType(customField.getFieldType())
                .formula(customField.getFormula())
                .dependentFieldKeys(customField.getDependentFieldKeys())
                .createdAt(customField.getCreatedDate())
                .updatedAt(customField.getModifiedDate())
                .build();
    }

    private CommonResponse validateUser(UserDO userDO, EntityManager entityManager) {
        BusinessUserRepository userRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessUserRepository.class);

        if (userDO == null) {
            return new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_UNAUTHORIZED);
        }
        if (userDO.getUserType().equals(ModelEnums.RoleType.PLATFORM.name())) {
            return new CommonResponse(AppConstants.STATUS_FORBIDDEN, AppConstants.MESSAGE_FORBIDDEN);
        }

        Optional<BusinessUser> userOpt = userRepository.findById(userDO.getUserId());
        return userOpt.map(businessUser -> new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, businessUser))
                .orElseGet(() -> new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_UNAUTHORIZED));
    }

    public String calculate(CustomField field, UUID entityId, EntityType entityType, EntityManager entityManager) {
        if (field.getFieldType() != ModelEnums.CustomFieldType.CALCULATED) {
            throw new IllegalStateException("Field is not calculated: " + field.getFieldKey());
        }

        ProductCustomFieldValueRepository valueRepository = tenantTransactionalUtil.getRepository(entityManager, ProductCustomFieldValueRepository.class);

        // 1. Load all dependency values for this entity
        Map<String, Object> variables = loadDependencies(
                valueRepository, field.getDependentFieldKeys(), entityId, entityType);

        // 2. Build SpEL context
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(variables);

        // 3. Evaluate formula
        // Formula uses #fieldKey syntax, e.g., "#unit_price * #quantity"
        Object result = parser.parseExpression(field.getFormula()).getValue(context);

        return result != null ? result.toString() : null;
    }

    private Map<String, Object> loadDependencies(ProductCustomFieldValueRepository valueRepository, Set<String> fieldKeys, UUID entityId, EntityType entityType) {

        List<CustomFieldValue> values = valueRepository
                .findByEntityAndFieldKeys(entityId, entityType, fieldKeys);

        return values.stream()
                .collect(Collectors.toMap(
                        v -> v.getCustomField().getFieldKey(),
                        v -> parseValue(v.getValue(), v.getCustomField().getDataType()),
                        (a, b) -> b
                ));
    }

    private Object parseValue(String raw, ModelEnums.CustomFieldDataType type) {
        if (raw == null) return null;
        return switch (type) {
            case NUMBER, DECIMAL -> Double.parseDouble(raw);
            case BOOLEAN -> Boolean.parseBoolean(raw);
            default -> raw;
        };
    }
}
