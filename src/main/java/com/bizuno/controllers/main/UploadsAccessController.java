package com.bizuno.controllers.main;

import com.bizuno.services.main.UploadsAccessService;
import com.bizuno.utils.FileUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadsAccessController {

    private final UploadsAccessService uploadsAccessService;
    private final FileUtility fileUtility;

    @GetMapping("/**")
    public ResponseEntity<Resource> getFile(HttpServletRequest request) {

        Resource resource = uploadsAccessService.getFile(
                request.getRequestURI().replaceFirst("/uploads/", "")
        );

        if (resource == null || !resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(fileUtility.getContentType(resource))
                .body(resource);
    }

}
