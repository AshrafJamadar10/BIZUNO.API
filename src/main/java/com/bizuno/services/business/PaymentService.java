package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreatePaymentRequestDTO;
import com.bizuno.dtos.business.PaymentResponseDTO;
import com.bizuno.dtos.business.UpdatePaymentRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.business.BusinessUser;
import com.bizuno.models.business.Customer;
import com.bizuno.models.business.Payment;
import com.bizuno.models.business.SalesInvoice;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.BusinessUserRepository;
import com.bizuno.repositories.business.CustomerRepository;
import com.bizuno.repositories.business.PaymentRepository;
import com.bizuno.repositories.business.SalesInvoiceRepository;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.utils.TenantTransactionalUtil;
import jakarta.persistence.EntityManager;
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
public class PaymentService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createPayment(CreatePaymentRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PaymentRepository paymentRepository = tenantTransactionalUtil.getRepository(entityManager, PaymentRepository.class);
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);
            SalesInvoiceRepository salesInvoiceRepository = tenantTransactionalUtil.getRepository(entityManager, SalesInvoiceRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            if (paymentRepository.existsByPaymentReference(request.getPaymentReference())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Payment with this reference"));
            }

            Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());
            if (customerOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Customer"));
            }

            SalesInvoice salesInvoice = null;
            if (request.getSalesInvoiceId() != null) {
                Optional<SalesInvoice> invoiceOpt = salesInvoiceRepository.findById(request.getSalesInvoiceId());
                if (invoiceOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Sales Invoice"));
                }
                salesInvoice = invoiceOpt.get();
            }

            Payment payment = Payment.builder()
                    .customer(customerOpt.get())
                    .salesInvoice(salesInvoice)
                    .paymentReference(request.getPaymentReference())
                    .paymentMethod(request.getPaymentMethod())
                    .amount(request.getAmount())
                    .receivedAt(request.getReceivedAt())
                    .status(request.getStatus())
                    .paymentMethodDetails(request.getPaymentMethodDetails())
                    .note(request.getNote())
                    .build();
            payment.prePersist();

            Payment savedPayment = paymentRepository.save(payment);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Payment"), mapPaymentToResponse(savedPayment));
        });
    }

    public CommonResponse updatePayment(UUID paymentId, UpdatePaymentRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PaymentRepository paymentRepository = tenantTransactionalUtil.getRepository(entityManager, PaymentRepository.class);
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);
            SalesInvoiceRepository salesInvoiceRepository = tenantTransactionalUtil.getRepository(entityManager, SalesInvoiceRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Payment"));
            }
            Payment payment = paymentOpt.get();

            if (paymentRepository.existsByPaymentReferenceAndPaymentIdNot(request.getPaymentReference(), paymentId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Payment with this reference"));
            }

            if (request.getCustomerId() != null) {
                Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());
                if (customerOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Customer"));
                }
                payment.setCustomer(customerOpt.get());
            }

            if (request.getSalesInvoiceId() != null) {
                Optional<SalesInvoice> invoiceOpt = salesInvoiceRepository.findById(request.getSalesInvoiceId());
                if (invoiceOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Sales Invoice"));
                }
                payment.setSalesInvoice(invoiceOpt.get());
            }

            payment.setPaymentReference(request.getPaymentReference());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setAmount(request.getAmount());
            payment.setReceivedAt(request.getReceivedAt());
            payment.setStatus(request.getStatus());
            payment.setPaymentMethodDetails(request.getPaymentMethodDetails());
            payment.setNote(request.getNote());
            payment.preUpdate();

            Payment updatedPayment = paymentRepository.save(payment);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Payment"), mapPaymentToResponse(updatedPayment));
        });
    }

    public CommonResponse getAllPayments(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PaymentRepository paymentRepository = tenantTransactionalUtil.getRepository(entityManager, PaymentRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "receivedAt" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Payment> paymentPage = paymentRepository.findAll(pageable);
            
            List<PaymentResponseDTO> content = paymentPage.getContent().stream()
                    .map(this::mapPaymentToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", paymentPage.getNumber());
            response.put("size", paymentPage.getSize());
            response.put("totalElements", paymentPage.getTotalElements());
            response.put("totalPages", paymentPage.getTotalPages());
            response.put("isLast", paymentPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getPaymentById(UUID paymentId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PaymentRepository paymentRepository = tenantTransactionalUtil.getRepository(entityManager, PaymentRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);

            return paymentOpt.map(payment -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Payment retrieved successfully", mapPaymentToResponse(payment))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Payment")));
        });
    }

    public CommonResponse deletePayment(UUID paymentId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            PaymentRepository paymentRepository = tenantTransactionalUtil.getRepository(entityManager, PaymentRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Payment"));
            }

            paymentRepository.delete(paymentOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Payment"));
        });
    }

    private PaymentResponseDTO mapPaymentToResponse(Payment payment){
        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .customerId(payment.getCustomer() != null ? payment.getCustomer().getCustomerId() : null)
                .customerName(payment.getCustomer() != null ? payment.getCustomer().getName() : null)
                .salesInvoiceId(payment.getSalesInvoice() != null ? payment.getSalesInvoice().getSalesInvoiceId() : null)
                .invoiceNumber(payment.getSalesInvoice() != null ? payment.getSalesInvoice().getInvoiceNumber() : null)
                .paymentReference(payment.getPaymentReference())
                .paymentMethod(payment.getPaymentMethod().name())
                .amount(payment.getAmount())
                .receivedAt(payment.getReceivedAt())
                .status(payment.getStatus().name())
                .paymentMethodDetails(payment.getPaymentMethodDetails())
                .note(payment.getNote())
                .build();
    }
    private CommonResponse validateUser(UserDO userDO, EntityManager entityManager){

        BusinessUserRepository userRepository = tenantTransactionalUtil.getRepository(entityManager, BusinessUserRepository.class);

        if(userDO == null){
            return new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_UNAUTHORIZED);
        }
        if (userDO.getUserType().equals(ModelEnums.RoleType.PLATFORM.name())){
            return new CommonResponse(AppConstants.STATUS_FORBIDDEN, AppConstants.MESSAGE_FORBIDDEN);
        }

        Optional<BusinessUser> userOpt = userRepository.findById(userDO.getUserId());
        return userOpt.map(businessUser -> new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, businessUser)).orElseGet(() -> new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_UNAUTHORIZED));

    }
}
