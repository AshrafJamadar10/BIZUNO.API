package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateTestRequest;
import com.bizuno.models.business.Test;
import com.bizuno.services.business.TestService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenant/test")
@AllArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping
    public ResponseEntity<Test> createTest(@RequestBody CreateTestRequest request) {
        return testService.createTest(request);
    }
}
