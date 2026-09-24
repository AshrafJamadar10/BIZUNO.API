package com.bizuno.repositories.business;

import com.bizuno.enums.ModelEnums;
import com.bizuno.models.business.CustomField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomFieldRepository extends JpaRepository<CustomField, UUID> {
    boolean existsByFieldKey(String fieldKey);

    boolean existsByFieldKeyAndFieldIdNot(String fieldKey, UUID fieldId);

    List<CustomField> findByStatus(ModelEnums.ProductStatus status);

    Optional<CustomField> findByFieldKey(String fieldKey);
}
