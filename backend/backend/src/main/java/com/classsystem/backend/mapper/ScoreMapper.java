package com.classsystem.backend.mapper;

import com.classsystem.backend.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ScoreMapper {

    @Select("SELECT * FROM score WHERE submission_id = #{submissionId}")
    Score findBySubmissionId(Long submissionId);

    void insert(Score score);

    void update(Score score);
}