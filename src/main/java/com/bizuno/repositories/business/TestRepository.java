package com.bizuno.repositories.business;

import com.bizuno.models.business.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
