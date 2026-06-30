package com.classsystem.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Assignment {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Integer totalScore;
    private String attachmentUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}