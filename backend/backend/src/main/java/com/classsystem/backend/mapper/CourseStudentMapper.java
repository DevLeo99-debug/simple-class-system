package com.classsystem.backend.mapper;

import com.classsystem.backend.entity.CourseStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface CourseStudentMapper {

    @Select("SELECT * FROM course_student WHERE course_id = #{courseId} AND student_id = #{studentId} AND status = 'ACTIVE'")
    CourseStudent findActiveRelation(Long courseId, Long studentId);

    @Select("SELECT * FROM course_student WHERE student_id = #{studentId} AND status = 'ACTIVE'")
    List<CourseStudent> findActiveByStudentId(Long studentId);

    @Select("SELECT * FROM course_student WHERE course_id = #{courseId} AND status = 'ACTIVE'")
    List<CourseStudent> findActiveByCourseId(Long courseId);

    void insert(CourseStudent courseStudent);

    @Update("UPDATE course_student SET status = 'WITHDRAWN' WHERE course_id = #{courseId} AND student_id = #{studentId}")
    void withdraw(Long courseId, Long studentId);
}