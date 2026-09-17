package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateReportRequestDTO;
import com.bizuno.dtos.business.ReportResponseDTO;
import com.bizuno.dtos.business.UpdateReportRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.Report;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.ReportRepository;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.utils.TenantTransactionalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createReport(CreateReportRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ReportRepository reportRepository = tenantTransactionalUtil.getRepository(entityManager, ReportRepository.class);

            Report report = Report.builder()
                    .reportType(request.getReportType())
                    .title(request.getTitle())
                    .generatedBy(request.getGeneratedBy())
                    .generatedAt(request.getGeneratedAt())
                    .fileUrl(request.getFileUrl())
                    .paramsJson(request.getParamsJson())
                    .build();
            report.prePersist();

            Report savedReport = reportRepository.save(report);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Report"), mapReportToResponse(savedReport));
        });
    }

    public CommonResponse updateReport(UUID reportId, UpdateReportRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ReportRepository reportRepository = tenantTransactionalUtil.getRepository(entityManager, ReportRepository.class);

            Optional<Report> reportOpt = reportRepository.findById(reportId);
            if (reportOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Report"));
            }
            Report report = reportOpt.get();

            report.setReportType(request.getReportType());
            report.setTitle(request.getTitle());
            report.setGeneratedBy(request.getGeneratedBy());
            report.setGeneratedAt(request.getGeneratedAt());
            report.setFileUrl(request.getFileUrl());
            report.setParamsJson(request.getParamsJson());
            report.preUpdate();

            Report updatedReport = reportRepository.save(report);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Report"), mapReportToResponse(updatedReport));
        });
    }

    public CommonResponse getAllReports(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ReportRepository reportRepository = tenantTransactionalUtil.getRepository(entityManager, ReportRepository.class);
            
            String sortProp = (sortBy == null || sortBy.isBlank()) ? "generatedAt" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Report> reportPage = reportRepository.findAll(pageable);
            
            List<ReportResponseDTO> content = reportPage.getContent().stream()
                    .map(this::mapReportToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", reportPage.getNumber());
            response.put("size", reportPage.getSize());
            response.put("totalElements", reportPage.getTotalElements());
            response.put("totalPages", reportPage.getTotalPages());
            response.put("isLast", reportPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getReportById(UUID reportId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ReportRepository reportRepository = tenantTransactionalUtil.getRepository(entityManager, ReportRepository.class);

            Optional<Report> reportOpt = reportRepository.findById(reportId);

            return reportOpt.map(report -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Report retrieved successfully", mapReportToResponse(report))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Report")));
        });
    }

    public CommonResponse deleteReport(UUID reportId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            ReportRepository reportRepository = tenantTransactionalUtil.getRepository(entityManager, ReportRepository.class);

            Optional<Report> reportOpt = reportRepository.findById(reportId);
            if (reportOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Report"));
            }

            reportRepository.delete(reportOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Report"));
        });
    }

    private ReportResponseDTO mapReportToResponse(Report report){
        return ReportResponseDTO.builder()
                .reportId(report.getReportId())
                .reportType(report.getReportType())
                .title(report.getTitle())
                .generatedBy(report.getGeneratedBy())
                .generatedAt(report.getGeneratedAt())
                .fileUrl(report.getFileUrl())
                .paramsJson(report.getParamsJson())
                .build();
    }
}
