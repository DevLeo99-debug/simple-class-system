package com.classsystem.backend.mapper;

import com.classsystem.backend.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface SubmissionMapper {

    @Select("SELECT * FROM submission WHERE id = #{id}")
    Submission findById(Long id);

    @Select("SELECT * FROM submission WHERE assignment_id = #{assignmentId} AND student_id = #{studentId}")
    Submission findByAssignmentAndStudent(Long assignmentId, Long studentId);

    @Select("SELECT * FROM submission WHERE assignment_id = #{assignmentId} ORDER BY submit_time DESC")
    List<Submission> findByAssignmentId(Long assignmentId);

    @Select("SELECT * FROM submission WHERE student_id = #{studentId} ORDER BY submit_time DESC")
    List<Submission> findByStudentId(Long studentId);

    void insert(Submission submission);

    void update(Submission submission);

    @Update("UPDATE submission SET status = 'GRADED' WHERE id = #{submissionId}")
    void markAsGraded(Long submissionId);
}