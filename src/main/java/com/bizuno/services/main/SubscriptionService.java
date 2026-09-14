package com.bizuno.services.main;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.CreateSubscriptionRequestDTO;
import com.bizuno.dtos.main.SubscriptionResponseDTO;
import com.bizuno.dtos.main.UpdateSubscriptionRequestDTO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.Business;
import com.bizuno.models.main.Package;
import com.bizuno.models.main.Subscription;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.repositories.main.PackageRepository;
import com.bizuno.repositories.main.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PackageRepository packageRepository;
    private final BusinessRepository businessRepository;

    public CommonResponse createSubscription(CreateSubscriptionRequestDTO createSubscriptionRequestDTO) {
        Optional<Package> packageOptional = packageRepository.findById(createSubscriptionRequestDTO.getPackageId());
        if (packageOptional.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Package"));
        }

        Optional<Business> businessOptional = businessRepository.findById(createSubscriptionRequestDTO.getBusinessId());
        if (businessOptional.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }

        Optional<Subscription> optionalSubscription = subscriptionRepository.findCurrentSubscriptionByBusinessCode(businessOptional.get().getBusinessCode());
        LocalDateTime startDate = optionalSubscription.map(value -> value.getEndDate().plusDays(1)).orElseGet(LocalDateTime::now);
        LocalDateTime endDate = startDate.plusMonths(packageOptional.get().getBillingPeriod().getMonths());

        Subscription subscription = Subscription.builder()
                .pack(packageOptional.get())
                .business(businessOptional.get())
                .subscriptionStatus(ModelEnums.SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .endDate(endDate)
                .autoRenew(createSubscriptionRequestDTO.getAutoRenew())
                .transactionId(createSubscriptionRequestDTO.getTransactionId())
                .invoiceNumber(createSubscriptionRequestDTO.getInvoiceNumber())
                .amountPaid(createSubscriptionRequestDTO.getAmountPaid())
                .paidThrough(createSubscriptionRequestDTO.getPaidThrough())
                .build();

        subscription.prePersist();

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return new CommonResponse(AppConstants.STATUS_CREATED, String.format(AppConstants.MESSAGE_CREATED, "Subscription"), getSubscriptionResponseDTO(savedSubscription));
    }

    public CommonResponse updateSubscription(UUID subscriptionId, UpdateSubscriptionRequestDTO updateSubscriptionRequestDTO) {
        Optional<Subscription> optionalSubscription = subscriptionRepository.findById(subscriptionId);
        if (optionalSubscription.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Subscription"));
        }

        Subscription subscription = optionalSubscription.get();

        Optional<Package> packageOptional = packageRepository.findById(updateSubscriptionRequestDTO.getPackageId());
        if (packageOptional.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Package"));
        }
        subscription.setPack(packageOptional.get());

        Optional<Business> businessOptional = businessRepository.findById(updateSubscriptionRequestDTO.getBusinessId());
        if (businessOptional.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        subscription.setBusiness(businessOptional.get());

        LocalDateTime startDate = updateSubscriptionRequestDTO.getStartDate();
        LocalDateTime endDate = startDate.plusMonths(packageOptional.get().getBillingPeriod().getMonths());
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);

        subscription.setSubscriptionStatus(updateSubscriptionRequestDTO.getSubscriptionStatus());
        subscription.setAutoRenew(updateSubscriptionRequestDTO.getAutoRenew());
        subscription.setTransactionId(updateSubscriptionRequestDTO.getTransactionId());
        subscription.setInvoiceNumber(updateSubscriptionRequestDTO.getInvoiceNumber());
        subscription.setAmountPaid(updateSubscriptionRequestDTO.getAmountPaid());
        subscription.setPaidThrough(updateSubscriptionRequestDTO.getPaidThrough());

        subscription.preUpdate();

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Subscription"), getSubscriptionResponseDTO(updatedSubscription));
    }

    public CommonResponse getAllSubscriptions(int page, int size, String sortBy, String sortDirection) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        String sortProp = (sortBy == null || sortBy.isBlank()) ? "createdDate" : sortBy;
        Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
        Page<Subscription> subscriptionPage = subscriptionRepository.findAll(pageable);

        List<SubscriptionResponseDTO> content = subscriptionPage.getContent()
                .stream()
                .map(this::getSubscriptionResponseDTO)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("page", subscriptionPage.getNumber());
        response.put("size", subscriptionPage.getSize());
        response.put("totalElements", subscriptionPage.getTotalElements());
        response.put("totalPages", subscriptionPage.getTotalPages());
        response.put("isLast", subscriptionPage.isLast());
        response.put("sortBy", sortProp);
        response.put("sortDirection", direction.toString());

        return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
    }

    public CommonResponse getSubscription(UUID subscriptionId) {
        Optional<Subscription> subscription = subscriptionRepository.findById(subscriptionId);

        return subscription.map(value -> 
                new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, getSubscriptionResponseDTO(value)))
                .orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Subscription")));
    }

    public CommonResponse deleteSubscription(UUID subscriptionId) {
        Optional<Subscription> subscription = subscriptionRepository.findById(subscriptionId);

        if (subscription.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Subscription"));
        }

        subscriptionRepository.deleteById(subscriptionId);

        return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Subscription"));
    }

    private SubscriptionResponseDTO getSubscriptionResponseDTO(Subscription subscription) {
        return SubscriptionResponseDTO.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .subscriptionStatus(subscription.getSubscriptionStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .trialEndDate(subscription.getTrialEndDate())
                .canceledAt(subscription.getCanceledAt())
                .gracePeriodEndsAt(subscription.getGracePeriodEndsAt())
                .isPaid(subscription.getIsPaid())
                .transactionId(subscription.getTransactionId())
                .invoiceNumber(subscription.getInvoiceNumber())
                .amountPaid(subscription.getAmountPaid())
                .paidThrough(subscription.getPaidThrough())
                .paidAt(subscription.getPaidAt())
                .autoRenew(subscription.getAutoRenew())
                .cancellationReason(subscription.getCancellationReason())
                .packageId(subscription.getPack() != null ? subscription.getPack().getPackageId() : null)
                .packageName(subscription.getPack() != null ? subscription.getPack().getName() : null)
                .businessId(subscription.getBusiness() != null ? subscription.getBusiness().getBusinessId() : null)
                .businessName(subscription.getBusiness() != null ? subscription.getBusiness().getBusinessName() : null)
                .businessCode(subscription.getBusiness() != null ? subscription.getBusiness().getBusinessCode() : null)
                .createdDate(subscription.getCreatedDate() != null ? subscription.getCreatedDate().toInstant() 
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .updatedDate(subscription.getModifiedDate() != null ? subscription.getModifiedDate().toInstant() 
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .isActive(subscription.getIsActive())
                .build();
    }
}
