package com.classsystem.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Course {
    private Long id;
    private String name;
    private String code;
    private Long teacherId;
    private String description;
    private String coverUrl;
    private String status;
    private Integer sortOrder;
    private String archiveType;
    private Integer studentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}