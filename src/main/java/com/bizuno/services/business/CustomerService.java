package com.bizuno.services.business;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.business.CreateCustomerRequestDTO;
import com.bizuno.dtos.business.CustomerResponseDTO;
import com.bizuno.dtos.business.UpdateCustomerRequestDTO;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.UserDO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.business.BusinessUser;
import com.bizuno.models.business.Customer;
import com.bizuno.models.main.Business;
import com.bizuno.repositories.business.BusinessUserRepository;
import com.bizuno.repositories.business.CustomerRepository;
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
public class CustomerService {

    private final BusinessRepository businessRepository;
    private final TenantTransactionalUtil tenantTransactionalUtil;

    public CommonResponse createCustomer(CreateCustomerRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            if (customerRepository.existsByName(request.getName())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Customer with this name"));
            }
            if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Customer with this email"));
            }
            if (request.getPhone() != null && customerRepository.existsByPhone(request.getPhone())) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Customer with this phone"));
            }

            Customer customer = Customer.builder()
                    .name(request.getName())
                    .contactPerson(request.getContactPerson())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .city(request.getCity())
                    .address(request.getAddress())
                    .gstNumber(request.getGstNumber())
                    .build();
            customer.prePersist();

            Customer savedCustomer = customerRepository.save(customer);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_CREATED, "Customer"), mapCustomerToResponse(savedCustomer));
        });
    }

    public CommonResponse updateCustomer(UUID customerId, UpdateCustomerRequestDTO request, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Customer> customerOpt = customerRepository.findById(customerId);
            if (customerOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Customer"));
            }
            Customer customer = customerOpt.get();

            if (customerRepository.existsByNameAndCustomerIdNot(request.getName(), customerId)){
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Customer with this name"));
            }
            if (request.getEmail() != null && customerRepository.existsByEmailAndCustomerIdNot(request.getEmail(), customerId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Customer with this email"));
            }
            if (request.getPhone() != null && customerRepository.existsByPhoneAndCustomerIdNot(request.getPhone(), customerId)) {
                return new CommonResponse(AppConstants.STATUS_CONFLICT, String.format(AppConstants.MESSAGE_EXISTS, "Customer with this phone"));
            }

            customer.setName(request.getName());
            customer.setContactPerson(request.getContactPerson());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());
            customer.setCity(request.getCity());
            customer.setAddress(request.getAddress());
            customer.setGstNumber(request.getGstNumber());
            customer.preUpdate();

            Customer updatedCustomer = customerRepository.save(customer);

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_UPDATED_SUCCESS, "Customer"), mapCustomerToResponse(updatedCustomer));
        });
    }

    public CommonResponse getAllCustomers(String businessCode, UserDO userDO, int page, int size, String sortBy, String sortDirection) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            String sortProp = (sortBy == null || sortBy.isBlank()) ? "name" : sortBy;
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));
            Page<Customer> customerPage = customerRepository.findAll(pageable);
            
            List<CustomerResponseDTO> content = customerPage.getContent().stream()
                    .map(this::mapCustomerToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("page", customerPage.getNumber());
            response.put("size", customerPage.getSize());
            response.put("totalElements", customerPage.getTotalElements());
            response.put("totalPages", customerPage.getTotalPages());
            response.put("isLast", customerPage.isLast());
            response.put("sortBy", sortProp);
            response.put("sortDirection", direction.toString());
            
            return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_SUCCESS, response);
        });
    }

    public CommonResponse getCustomerById(UUID customerId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Customer> customerOpt = customerRepository.findById(customerId);

            return customerOpt.map(customer -> new CommonResponse(AppConstants.STATUS_SUCCESS, "Customer retrieved successfully", mapCustomerToResponse(customer))).orElseGet(() -> new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Customer")));
        });
    }

    public CommonResponse deleteCustomer(UUID customerId, String businessCode, UserDO userDO) {
        Optional<Business> businessOpt = businessRepository.findByBusinessCode(businessCode);
        if (businessOpt.isEmpty()) {
            return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Business"));
        }
        Business business = businessOpt.get();

        return tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), entityManager -> {
            CustomerRepository customerRepository = tenantTransactionalUtil.getRepository(entityManager, CustomerRepository.class);

            CommonResponse validateUser = validateUser(userDO, entityManager);
            if (validateUser.getStatus() != AppConstants.STATUS_SUCCESS) {
                return validateUser;
            }

            Optional<Customer> customerOpt = customerRepository.findById(customerId);
            if (customerOpt.isEmpty()) {
                return new CommonResponse(AppConstants.STATUS_NOT_FOUND, String.format(AppConstants.NOT_FOUND, "Customer"));
            }

            customerRepository.delete(customerOpt.get());

            return new CommonResponse(AppConstants.STATUS_SUCCESS, String.format(AppConstants.MESSAGE_DELETED_SUCCESS, "Customer"));
        });
    }

    private CustomerResponseDTO mapCustomerToResponse(Customer customer){
        return CustomerResponseDTO.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .contactPerson(customer.getContactPerson())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .city(customer.getCity())
                .address(customer.getAddress())
                .gstNumber(customer.getGstNumber())
                .outstandingBalance(customer.getOutstandingBalance())
                .status(customer.getStatus().name())
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
