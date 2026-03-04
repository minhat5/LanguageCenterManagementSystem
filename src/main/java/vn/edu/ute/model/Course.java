package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.edu.ute.common.enumeration.DurationUnit;
import vn.edu.ute.common.enumeration.Level;
import vn.edu.ute.common.enumeration.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "duration_unit")
    @Enumerated(EnumType.STRING)
    private DurationUnit durationUnit;

    @Column(name = "fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal fee;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Course(Long courseId, String courseName, String description, Level level, Integer duration, DurationUnit durationUnit, BigDecimal fee, Status status) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.level = level;
        this.duration = duration;
        this.durationUnit = durationUnit;
        this.fee = fee;
        this.status = status;
    }
}
