package com.bizuno.repositories.business;

import com.bizuno.models.business.CustomFieldValue;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProductCustomFieldValueRepository extends JpaRepository<CustomFieldValue, UUID> {
    List<CustomFieldValue> findByProductProductId(UUID productId);

    Optional<CustomFieldValue> findByProductProductIdAndCustomFieldFieldId(UUID productId, UUID customFieldId);

    void deleteByProductProductId(UUID productId);

    void deleteByCustomFieldFieldId(UUID customFieldId);

    List<CustomFieldValue> findByEntityAndFieldKeys(UUID entityId, EntityType entityType, Set<String> fieldKeys);
}
