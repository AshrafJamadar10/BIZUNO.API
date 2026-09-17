package com.bizuno.repositories.business;

import com.bizuno.models.business.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance, UUID> {
    boolean existsByStaffIdAndAttendanceDate(UUID staffId, LocalDate attendanceDate);
}
