package vn.edu.ute.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ScheduleView (
        Long scheduleId,
        String className,
        LocalDate studyDate,
        LocalTime startTime,
        LocalTime endTime,
        String branchName,
        String roomName,
        LocalDateTime createdAt
) {
}
