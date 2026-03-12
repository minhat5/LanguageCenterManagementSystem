package vn.edu.ute.dto;

import vn.edu.ute.common.enumeration.ClassStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClasView(
        Long classId,
        String className,
        String courseName,
        String teacherName,
        String roomName,
        String branchName,
        LocalDate startDate,
        LocalDate endDate,
        Integer maxStudent,
        ClassStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
