package com.classsystem.backend.service;

import com.classsystem.backend.dto.AssignmentCreateDTO;
import com.classsystem.backend.dto.ScoreDTO;
import com.classsystem.backend.dto.SubmissionDTO;
import com.classsystem.backend.entity.Assignment;
import com.classsystem.backend.entity.Submission;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AssignmentService {
    Assignment createAssignment(Long teacherId, AssignmentCreateDTO dto, MultipartFile file);
    List<Assignment> getCourseAssignments(Long courseId);
    List<Submission> getSubmissions(Long assignmentId);
    void gradeSubmission(Long teacherId, ScoreDTO dto);
    void batchGrade(Long teacherId, List<ScoreDTO> scores);
    void urgeSubmission(Long teacherId, Long assignmentId);
    void closeAssignment(Long teacherId, Long assignmentId);
    Submission submitAssignment(Long studentId, SubmissionDTO dto, MultipartFile file);
    List<Submission> getStudentSubmissions(Long studentId, Long courseId);
}