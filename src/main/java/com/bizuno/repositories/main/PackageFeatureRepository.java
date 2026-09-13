package com.bizuno.repositories.main;

import com.bizuno.models.main.PackageFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PackageFeatureRepository extends JpaRepository<PackageFeature, UUID> {
}
