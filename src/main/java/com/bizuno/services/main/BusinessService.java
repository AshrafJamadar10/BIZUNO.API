package com.bizuno.services.main;

import com.bizuno.config.BusinessDatabaseConfig;
import com.bizuno.dtos.business.CreateTenantRequestDTO;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.main.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final DatabaseCreationService databaseCreationService;
    private final BusinessDatabaseConfig businessDatabaseConfig;

    @Value("${packages-to-scan.business}")
    private String tenantPackagesToScan;

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
                           DatabaseCreationService databaseCreationService,
                           BusinessDatabaseConfig businessDatabaseConfig) {
        this.businessRepository = businessRepository;
        this.databaseCreationService = databaseCreationService;
        this.businessDatabaseConfig = businessDatabaseConfig;
    }

    public ResponseEntity<Business> createTenant(CreateTenantRequestDTO createTenantRequestDTO) {
        System.out.println("🚀 Starting tenant creation for: " + createTenantRequestDTO.getName());
        
        Business business = new Business();
        createTenantRequestDTO.setDbName(createTenantRequestDTO.getDbName().toLowerCase());
        business.setBusinessName(createTenantRequestDTO.getName());
        business.setEmail(createTenantRequestDTO.getEmail());
        business.setDbName(createTenantRequestDTO.getDbName());
        business.setTenantId(UUID.randomUUID().toString());

        Business savedBusiness = businessRepository.save(business);
        System.out.println("✅ Tenant saved with ID: " + savedBusiness.getId());

        databaseCreationService.createTenantDatabase(createTenantRequestDTO.getDbName());
        System.out.println("✅ Database created: " + createTenantRequestDTO.getDbName());

        System.out.println("🔧 Creating EntityManagerFactory for tenant ID: " + business.getTenantId());
        businessDatabaseConfig.getOrCreateTenantEntityManagerFactory(
            business.getTenantId(),
            createTenantRequestDTO.getDbName(),
            tenantPackagesToScan
        );
        System.out.println("✅ EntityManagerFactory creation completed");

        return ResponseEntity.ok(savedBusiness);
    }

    public ResponseEntity<List<Business>> getAllTenants() {
        List<Business> businesses = businessRepository.findAll();
        return ResponseEntity.ok(businesses);
    }

    public ResponseEntity<Void> deleteTenant(UUID id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + id));

        businessDatabaseConfig.closeTenantEntityManagerFactory(id.toString());
        databaseCreationService.dropTenantDatabase(business.getDbName());
        businessRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
