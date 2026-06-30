package com.classsystem.backend.mapper;

import com.classsystem.backend.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface CourseMapper {

    @Select("SELECT * FROM course WHERE id = #{id}")
    Course findById(Long id);

    @Select("SELECT * FROM course WHERE teacher_id = #{teacherId} AND status = 'ACTIVE' ORDER BY sort_order ASC")
    List<Course> findByTeacherId(Long teacherId);

    @Select("SELECT * FROM course WHERE code = #{code} AND status = 'ACTIVE'")
    Course findByCode(String code);

    @Select("SELECT * FROM course WHERE teacher_id = #{teacherId} AND status = 'ARCHIVED'")
    List<Course> findArchivedByTeacherId(Long teacherId);

    void insert(Course course);

    void update(Course course);

    void deleteById(Long id);

    @Update("UPDATE course SET student_count = student_count + 1 WHERE id = #{courseId}")
    void incrementStudentCount(Long courseId);

    @Update("UPDATE course SET student_count = student_count - 1 WHERE id = #{courseId}")
    void decrementStudentCount(Long courseId);
}