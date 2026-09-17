package com.bizuno.controllers.business;

import com.bizuno.dtos.business.CreateReportRequestDTO;
import com.bizuno.dtos.business.UpdateReportRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.services.business.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bizuno/business/{businessCode}/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody CreateReportRequestDTO request,
                                          @PathVariable String businessCode,
                                          @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.createReport(request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
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
    public ResponseEntity<?> getReportById(@PathVariable UUID reportId,
                                          @PathVariable String businessCode,
                                          @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.getReportById(reportId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<?> updateReport(@PathVariable UUID reportId,
                                        @RequestBody UpdateReportRequestDTO request,
                                        @PathVariable String businessCode,
                                        @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.updateReport(reportId, request, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteReport(@PathVariable UUID reportId,
                                          @PathVariable String businessCode,
                                          @RequestAttribute UserDO userDO) {
        CommonResponse response = reportService.deleteReport(reportId, businessCode, userDO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
