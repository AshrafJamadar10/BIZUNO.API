package com.bizuno.dtos.business;

import com.bizuno.enums.ModelEnums;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class UpdateStaffAttendanceRequestDTO {
    @NotNull(message = "Attendance date is required")
    @JsonFormat(pattern = "dd-MM-yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate attendanceDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.AttendanceStatus status;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime checkIn;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime checkOut;

    private String note;
}
