package vn.edu.ute.dto;

import vn.edu.ute.common.enumeration.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceView(
        Long attendanceId,
        String studentName,
        String className,
        LocalDate attendDate,
        AttendanceStatus status,
        String note,
        LocalDateTime createdAt
) {
}
