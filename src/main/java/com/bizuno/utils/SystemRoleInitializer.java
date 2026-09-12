package com.bizuno.utils;

import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.Role;
import com.bizuno.models.main.RoleCrudPermission;
import com.bizuno.repositories.main.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SystemRoleInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SystemRoleInitializer.class);

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Optional<Role> superAdmin = roleRepository.findByName("SUPER_ADMIN");

        if (superAdmin.isEmpty()) {
            superAdmin = Optional.of(createSuperAdminRole());
        }

        syncSuperAdminPermissions(superAdmin.get());
    }

    private Role createSuperAdminRole() {
        Role role = new Role();
        role.setName("SUPER_ADMIN");
        role.setTitle(ModelEnums.Titles.SUPER_ADMIN.name());
        role.setDescription("System generated role with full access");
        role.setCreatedBy("SYSTEM");
        role.setType(ModelEnums.RoleType.PLATFORM.name());

        roleRepository.save(role);
        logger.info("SUPER_ADMIN role created");

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

