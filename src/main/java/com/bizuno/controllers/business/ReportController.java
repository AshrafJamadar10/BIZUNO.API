package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateReportRequestDTO;
import com.bizuno.dtos.business.UpdateReportRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @PreAuthorize("hasAuthority('REPORT_CREATE')")
    public ResponseEntity<?> createReport(@RequestBody CreateReportRequestDTO request,
                                          @PathVariable String businessCode,
                                          @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.createReport(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<?> getAllReports(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(required = false) String sortDirection,
                                           @PathVariable String businessCode,
                                           @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.getAllReports(businessCode, userDO, page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<?> getReportById(@PathVariable UUID reportId,
                                          @PathVariable String businessCode,
                                          @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.getReportById(reportId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_UPDATE')")
    public ResponseEntity<?> updateReport(@PathVariable UUID reportId,
                                        @RequestBody UpdateReportRequestDTO request,
                                        @PathVariable String businessCode,
                                        @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.updateReport(reportId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_DELETE')")
    public ResponseEntity<?> deleteReport(@PathVariable UUID reportId,
                                          @PathVariable String businessCode,
                                          @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.deleteReport(reportId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
