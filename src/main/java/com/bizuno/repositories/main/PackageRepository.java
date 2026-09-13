package com.bizuno.repositories.main;

import com.bizuno.models.main.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PackageRepository extends JpaRepository<Package, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
