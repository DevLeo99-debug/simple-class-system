package com.classsystem.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CourseSortDTO {
    private List<Long> courseIds;
}