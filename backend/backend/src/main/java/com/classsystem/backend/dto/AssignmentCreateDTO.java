package com.classsystem.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentCreateDTO {
    private Long courseId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Integer totalScore;
}