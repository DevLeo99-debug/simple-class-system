package com.classsystem.backend.mapper;

import com.classsystem.backend.entity.Assignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AssignmentMapper {

    @Select("SELECT * FROM assignment WHERE id = #{id}")
    Assignment findById(Long id);

    @Select("SELECT * FROM assignment WHERE course_id = #{courseId} ORDER BY created_at DESC")
    List<Assignment> findByCourseId(Long courseId);

    void insert(Assignment assignment);

    void update(Assignment assignment);
}