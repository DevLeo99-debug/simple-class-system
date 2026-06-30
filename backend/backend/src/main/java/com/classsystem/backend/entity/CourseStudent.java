package com.classsystem.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseStudent {
    private Long id;
    private Long courseId;
    private Long studentId;
    private LocalDateTime joinedAt;
    private String status;
}