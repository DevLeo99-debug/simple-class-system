package com.classsystem.backend.dto;

import lombok.Data;

@Data
public class ScoreDTO {
    private Long submissionId;
    private Integer score;
    private String comment;
}