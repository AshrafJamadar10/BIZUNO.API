package com.bizuno.repositories.business;

import com.bizuno.models.business.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, UUID> {
    boolean existsByEmailAndStaffIdNot(String email, UUID staffId);
    boolean existsByEmail(String email);
    boolean existsByPhoneAndStaffIdNot(String phone, UUID staffId);
    boolean existsByPhone(String phone);
}
