package com.bizuno.config;

import com.bizuno.models.main.Business;
import com.bizuno.repositories.main.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BusinessStartupInitializer implements CommandLineRunner {

    private final BusinessRepository businessRepository;
    private final BusinessDatabaseConfig businessDatabaseConfig;

    @Value("${packages-to-scan.business}")
    private String tenantPackagesToScan;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Initializing tenant EntityManagerFactories on startup...");
        
        List<Business> businesses = businessRepository.findAll();
        System.out.println("📊 Found " + businesses.size() + " existing tenants in database");
        
        for (Business business : businesses) {
            try {
                System.out.println("🔧 Recreating EntityManagerFactory for tenant: " + business.getTenantId() +
                                 " (database: " + business.getDbName() + ")");
                
                businessDatabaseConfig.getOrCreateTenantEntityManagerFactory(
                    business.getTenantId(),
                    business.getDbName(),
                    tenantPackagesToScan
                );
                
                System.out.println("✅ EntityManagerFactory recreated for tenant: " + business.getTenantId());
            } catch (Exception e) {
                System.err.println("❌ Failed to recreate EntityManagerFactory for tenant: " + business.getTenantId());
                e.printStackTrace();
            }
        }
        
        System.out.println("✅ Tenant initialization completed");
    }
}
