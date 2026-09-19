package com.bizuno.config;

import com.bizuno.models.main.Business;
import com.bizuno.repositories.main.BusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessStartupInitializer implements CommandLineRunner {

    private final BusinessRepository businessRepository;
    private final BusinessDatabaseConfig businessDatabaseConfig;

    @Value("${packages-to-scan.business}")
    private String tenantPackagesToScan;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing tenant EntityManagerFactories on startup...");

        List<Business> businesses = businessRepository.findAll();
        log.info("Found {} existing tenants in database", businesses.size());

        for (Business business : businesses) {
            try {
                log.info("Recreating EntityManagerFactory for tenant: {} (database: {})",
                        business.getTenantId(), business.getDbName());

                businessDatabaseConfig.getOrCreateTenantEntityManagerFactory(
                    business.getTenantId(),
                    business.getDbName(),
                    tenantPackagesToScan
                );

                log.info("EntityManagerFactory recreated for tenant: {}", business.getTenantId());
            } catch (Exception e) {
                log.error("Failed to recreate EntityManagerFactory for tenant: {}", business.getTenantId(), e);
            }
        }

        log.info("Tenant initialization completed");
    }
}
