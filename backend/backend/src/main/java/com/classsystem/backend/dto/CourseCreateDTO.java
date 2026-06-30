package com.classsystem.backend.dto;

import lombok.Data;

@Data
public class CourseCreateDTO {
    private String name;
    private String description;
    private String coverUrl;
}