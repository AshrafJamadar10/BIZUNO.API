package com.bizuno.services.business;

import com.bizuno.dtos.main.CreateTestRequest;
import com.bizuno.models.main.Business;
import com.bizuno.models.business.Test;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.repositories.business.TestRepository;
import com.bizuno.utils.TenantTransactionalUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TestService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public ResponseEntity<Test> createTest(CreateTestRequest request) {
        Optional<Business> tenantOptional = businessRepository.findByTenantId(request.getId());
        if (tenantOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Business business = tenantOptional.get();

        Test test = tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), em -> {

            TestRepository testRepository = tenantTransactionalUtil.getRepository(em, TestRepository.class);

            Test newTest = Test.builder()
                    .name(request.getName())
                    .build();

            return testRepository.save(newTest);
        });

        return ResponseEntity.ok(test);
    }
}
