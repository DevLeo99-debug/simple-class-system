package com.classsystem.backend.dto;

import lombok.Data;

@Data
public class CourseUpdateDTO {
    private Long id;
    private String name;
    private String description;
    private String coverUrl;
}