package com.bizuno.utils;

import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.Role;
import com.bizuno.models.main.RoleCrudPermission;
import com.bizuno.models.main.User;
import com.bizuno.repositories.main.RoleRepository;
import com.bizuno.repositories.main.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SystemUserInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SystemUserInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SystemRoleInitializer systemRoleInitializer;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${system.super-admin.email:superadmin@bizuno.com}")
    private String superAdminEmail;

    @Value("${system.super-admin.phone:9876543210}")
    private String superAdminPhone;

    @Value("${system.super-admin.password:Bizuno@123}")
    private String superAdminPassword;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                .orElseGet(this::createDefaultSuperAdminRole);

        boolean userExists = userRepository.findAll().stream()
                .anyMatch(user ->
                        (user.getEmail() != null && user.getEmail().equalsIgnoreCase(superAdminEmail))
                                || (user.getPhoneNumber() != null && user.getPhoneNumber().equals(superAdminPhone)));

        if (userExists) {
            logger.info("System super admin user already exists");
            return;
        }

        User systemUser = User.builder()
                .email(superAdminEmail)
                .phoneNumber(superAdminPhone)
                .password(passwordEncoder.encode(superAdminPassword))
                .role(superAdminRole)
                .build();

        systemUser.setCreatedBy("SYSTEM");

        userRepository.save(systemUser);

        logger.info("System super admin user created with email {}", superAdminEmail);
    }

    private Role createDefaultSuperAdminRole() {
        Role role = new Role();
        role.setName("SUPER_ADMIN");
        role.setTitle(ModelEnums.Titles.SUPER_ADMIN.name());
        role.setDescription("System generated role with full access");
        role.setCreatedBy("SYSTEM");
        role.setType(ModelEnums.RoleType.PLATFORM.name());
        role = roleRepository.save(role);

        syncSuperAdminPermissions(role);
        return role;
    }

    private void syncSuperAdminPermissions(Role role) {

        // Enum = source of truth
        Set<String> enumOperations = Arrays.stream(ModelEnums.CrudOperation.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Existing permissions mapped by scope
        Map<String, RoleCrudPermission> existingPermissions =
                role.getCrudPermissions()
                        .stream()
                        .collect(Collectors.toMap(
                                RoleCrudPermission::getScope,
                                p -> p
                        ));

        for (ModelEnums.CrudPermissionScopes scopeEnum :
                ModelEnums.CrudPermissionScopes.values()) {

            String scope = scopeEnum.name();

            RoleCrudPermission permission =
                    existingPermissions.get(scope);

            if (permission == null) {
                // New scope added in enum → create once
                permission = new RoleCrudPermission();
                permission.setScope(scope);
                permission.setOperations(new HashSet<>(enumOperations));
                role.addPermission(permission);

                logger.info("Added new permission scope {}", scope);
                continue;
            }

            // 🔁 SYNC OPERATIONS IN-PLACE

            // Add new operations
            for (String op : enumOperations) {
                if (!permission.getOperations().contains(op)) {
                    permission.getOperations().add(op);
                    logger.info("Added operation {} to scope {}", op, scope);
                }
            }

            // Remove deleted operations
            permission.getOperations().removeIf(op -> !enumOperations.contains(op));
        }

        // Remove permissions whose scope no longer exists in enum
        Set<String> enumScopes = Arrays.stream(ModelEnums.CrudPermissionScopes.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        role.getCrudPermissions()
                .removeIf(p -> !enumScopes.contains(p.getScope()));

        roleRepository.save(role);
        logger.info("SUPER_ADMIN permissions synced incrementally");
    }
}
