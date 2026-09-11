package com.bizuno.utils;

import com.bizuno.config.BusinessDatabaseConfig;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TenantTransactionalUtil {

    private final BusinessDatabaseConfig businessDatabaseConfig;

    public <T> T excecuteInTenantContext(String tenantId, String dbName, Function<EntityManager, T> operation){
        return businessDatabaseConfig.executeInTenantContext(tenantId, dbName, operation);
    }

    public <T, R extends JpaRepository<T, ?>> R getRepository(EntityManager em, Class<R> repositoryInterface) {
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(em);
        return jpaRepositoryFactory.getRepository(repositoryInterface);
    }
}
