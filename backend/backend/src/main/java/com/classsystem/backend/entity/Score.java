package com.classsystem.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Score {
    private Long id;
    private Long submissionId;
    private Integer score;
    private String comment;
    private Long gradedBy;
    private LocalDateTime gradedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}