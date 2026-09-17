package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateSalesInvoiceItemRequestDTO;
import com.bizuno.dtos.business.CreateSalesInvoiceRequestDTO;
import com.bizuno.dtos.business.SalesInvoiceItemResponseDTO;
import com.bizuno.dtos.business.SalesInvoiceResponseDTO;
import com.bizuno.dtos.business.UpdateSalesInvoiceRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.models.business.Customer;
import com.bizuno.models.business.Product;
import com.bizuno.models.business.SalesInvoice;
import com.bizuno.models.business.SalesInvoiceItem;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.CustomerRepository;
import com.bizuno.repositories.business.ProductRepository;
import com.bizuno.repositories.business.SalesInvoiceItemRepository;
import com.bizuno.repositories.business.SalesInvoiceRepository;
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
public class SalesInvoiceService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createSalesInvoice(CreateSalesInvoiceRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SalesInvoiceRepository salesInvoiceRepository = tenantTransactionalUtil.getRepository(entityManager, SalesInvoiceRepository.class);
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);
            ProductRepository productRepository = tenantTransactionalUtil.getRepository(entityManager, ProductRepository.class);
            SalesInvoiceItemRepository salesInvoiceItemRepository = tenantTransactionalUtil.getRepository(entityManager, SalesInvoiceItemRepository.class);

            if (salesInvoiceRepository.existsByInvoiceNumber(request.getInvoiceNumber())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Invoice with this number"));
            }

            Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());
            if (customerOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Customer"));
            }

            SalesInvoice salesInvoice = SalesInvoice.builder()
                    .customer(customerOpt.get())
                    .invoiceNumber(request.getInvoiceNumber())
                    .issuedAt(request.getIssuedAt())
                    .dueDate(request.getDueDate())
                    .subtotal(request.getSubtotal())
                    .taxAmount(request.getTaxAmount())
                    .total(request.getTotal())
                    .amountPaid(request.getAmountPaid())
                    .balance(request.getBalance())
                    .status(request.getStatus())
                    .paymentStatus(request.getPaymentStatus())
                    .salesperson(request.getSalesperson())
                    .items(new ArrayList<>())
                    .build();
            salesInvoice.prePersist();

            SalesInvoice savedInvoice = salesInvoiceRepository.save(salesInvoice);

            if (request.getItems() != null && !request.getItems().isEmpty()) {
                for (CreateSalesInvoiceItemRequestDTO itemRequest : request.getItems()) {
                    Optional<Product> productOpt = productRepository.findById(itemRequest.getProductId());
                    if (productOpt.isEmpty()) {
                        return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Product"));
                    }

                    SalesInvoiceItem item = SalesInvoiceItem.builder()
                            .salesInvoice(savedInvoice)
                            .product(productOpt.get())
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(itemRequest.getUnitPrice())
                            .discount(itemRequest.getDiscount())
                            .tax(itemRequest.getTax())
                            .total(itemRequest.getTotal())
                            .description(itemRequest.getDescription())
                            .build();
                    item.prePersist();
                    salesInvoiceItemRepository.save(item);
                    savedInvoice.getItems().add(item);
                }
            }

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Sales Invoice"), mapSalesInvoiceToResponse(savedInvoice));
        });
    }

    public CommonResponse updateSalesInvoice(UUID salesInvoiceId, UpdateSalesInvoiceRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SalesInvoiceRepository salesInvoiceRepository = tenantTransactionalUtil.getRepository(entityManager, SalesInvoiceRepository.class);
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);

            Optional<SalesInvoice> invoiceOpt = salesInvoiceRepository.findById(salesInvoiceId);
            if (invoiceOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Sales Invoice"));
            }
            SalesInvoice salesInvoice = invoiceOpt.get();

            if (salesInvoiceRepository.existsByInvoiceNumberAndSalesInvoiceIdNot(request.getInvoiceNumber(), salesInvoiceId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Invoice with this number"));
            }

            if (request.getCustomerId() != null) {
                Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());
                if (customerOpt.isEmpty()) {
                    return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Customer"));
                }
                salesInvoice.setCustomer(customerOpt.get());
            }

            salesInvoice.setInvoiceNumber(request.getInvoiceNumber());
            salesInvoice.setIssuedAt(request.getIssuedAt());
            salesInvoice.setDueDate(request.getDueDate());
            salesInvoice.setSubtotal(request.getSubtotal());
            salesInvoice.setTaxAmount(request.getTaxAmount());
            salesInvoice.setTotal(request.getTotal());
            salesInvoice.setAmountPaid(request.getAmountPaid());
            salesInvoice.setBalance(request.getBalance());
            salesInvoice.setStatus(request.getStatus());
            salesInvoice.setPaymentStatus(request.getPaymentStatus());
            salesInvoice.setSalesperson(request.getSalesperson());
            salesInvoice.preUpdate();

            SalesInvoice updatedInvoice = salesInvoiceRepository.save(salesInvoice);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Sales Invoice"), mapSalesInvoiceToResponse(updatedInvoice));
        });
    }

    public CommonResponse getAllSalesInvoices(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SalesInvoiceRepository salesInvoiceRepository = tenantTransactionalUtil.getRepository(entityManager, SalesInvoiceRepository.class);
            
            String sortProp = (sortBy == null || sortBy.isBlank()) ? "issuedAt" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<SalesInvoice> invoicePage = salesInvoiceRepository.findAll(pageable);
            
            List<SalesInvoiceResponseDTO> content = invoicePage.getContent().stream()
                    .map(this::mapSalesInvoiceToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", invoicePage.getNumber());
            response.put("size", invoicePage.getSize());
            response.put("totalElements", invoicePage.getTotalElements());
            response.put("totalPages", invoicePage.getTotalPages());
            response.put("isLast", invoicePage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getSalesInvoiceById(UUID salesInvoiceId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SalesInvoiceRepository salesInvoiceRepository = tenantTransactionalUtil.getRepository(entityManager, SalesInvoiceRepository.class);

            Optional<SalesInvoice> invoiceOpt = salesInvoiceRepository.findById(salesInvoiceId);

            return invoiceOpt.map(invoice -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Sales Invoice retrieved successfully", mapSalesInvoiceToResponse(invoice))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Sales Invoice")));
        });
    }

    public CommonResponse deleteSalesInvoice(UUID salesInvoiceId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            SalesInvoiceRepository salesInvoiceRepository = tenantTransactionalUtil.getRepository(entityManager, SalesInvoiceRepository.class);

            Optional<SalesInvoice> invoiceOpt = salesInvoiceRepository.findById(salesInvoiceId);
            if (invoiceOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Sales Invoice"));
            }

            salesInvoiceRepository.delete(invoiceOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Sales Invoice"));
        });
    }

    private SalesInvoiceResponseDTO mapSalesInvoiceToResponse(SalesInvoice invoice){
        List<SalesInvoiceItemResponseDTO> items = invoice.getItems().stream()
                .map(this::mapSalesInvoiceItemToResponse)
                .collect(Collectors.toList());

        return SalesInvoiceResponseDTO.builder()
                .salesInvoiceId(invoice.getSalesInvoiceId())
                .customerId(invoice.getCustomer() != null ? invoice.getCustomer().getCustomerId() : null)
                .customerName(invoice.getCustomer() != null ? invoice.getCustomer().getName() : null)
                .invoiceNumber(invoice.getInvoiceNumber())
                .issuedAt(invoice.getIssuedAt())
                .dueDate(invoice.getDueDate())
                .subtotal(invoice.getSubtotal())
                .taxAmount(invoice.getTaxAmount())
                .total(invoice.getTotal())
                .amountPaid(invoice.getAmountPaid())
                .balance(invoice.getBalance())
                .status(invoice.getStatus().name())
                .paymentStatus(invoice.getPaymentStatus().name())
                .salesperson(invoice.getSalesperson())
                .items(items)
                .build();
    }

    private SalesInvoiceItemResponseDTO mapSalesInvoiceItemToResponse(SalesInvoiceItem item){
        return SalesInvoiceItemResponseDTO.builder()
                .salesInvoiceItemId(item.getSalesInvoiceItemId())
                .productId(item.getProduct() != null ? item.getProduct().getProductId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discount(item.getDiscount())
                .tax(item.getTax())
                .total(item.getTotal())
                .description(item.getDescription())
                .build();
    }
}
