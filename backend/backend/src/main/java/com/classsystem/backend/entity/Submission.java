package com.classsystem.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Submission {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String fileUrl;
    private String content;
    private LocalDateTime submitTime;
    private Integer isLate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}