package com.classsystem.backend.controller;

import com.classsystem.backend.dto.AssignmentCreateDTO;
import com.classsystem.backend.dto.ScoreDTO;
import com.classsystem.backend.dto.SubmissionDTO;
import com.classsystem.backend.entity.Assignment;
import com.classsystem.backend.entity.Submission;
import com.classsystem.backend.result.Result;
import com.classsystem.backend.service.AssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@CrossOrigin
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/create")
    public Result<Assignment> createAssignment(HttpServletRequest request,
                                               @RequestPart("data") AssignmentCreateDTO dto,
                                               @RequestPart(value = "file", required = false) MultipartFile file) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(assignmentService.createAssignment(userId, dto, file));
    }

    @GetMapping("/course/{courseId}")
    public Result<List<Assignment>> getCourseAssignments(@PathVariable Long courseId) {
        return Result.success(assignmentService.getCourseAssignments(courseId));
    }

    @GetMapping("/{assignmentId}/submissions")
    public Result<List<Submission>> getSubmissions(@PathVariable Long assignmentId) {
        return Result.success(assignmentService.getSubmissions(assignmentId));
    }

    @PostMapping("/grade")
    public Result<Void> gradeSubmission(HttpServletRequest request, @RequestBody ScoreDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        assignmentService.gradeSubmission(userId, dto);
        return Result.success(null);
    }

    @PostMapping("/batch-grade")
    public Result<Void> batchGrade(HttpServletRequest request, @RequestBody List<ScoreDTO> scores) {
        Long userId = (Long) request.getAttribute("userId");
        assignmentService.batchGrade(userId, scores);
        return Result.success(null);
    }

    @PostMapping("/{assignmentId}/urge")
    public Result<Void> urgeSubmission(HttpServletRequest request, @PathVariable Long assignmentId) {
        Long userId = (Long) request.getAttribute("userId");
        assignmentService.urgeSubmission(userId, assignmentId);
        return Result.success(null);
    }

    @PutMapping("/{assignmentId}/close")
    public Result<Void> closeAssignment(HttpServletRequest request, @PathVariable Long assignmentId) {
        Long userId = (Long) request.getAttribute("userId");
        assignmentService.closeAssignment(userId, assignmentId);
        return Result.success(null);
    }

    @PostMapping("/submit")
    public Result<Submission> submitAssignment(HttpServletRequest request,
                                               @RequestPart("data") SubmissionDTO dto,
                                               @RequestPart(value = "file", required = false) MultipartFile file) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(assignmentService.submitAssignment(userId, dto, file));
    }

    @GetMapping("/my")
    public Result<List<Submission>> getMySubmissions(HttpServletRequest request,
                                                     @RequestParam(required = false) Long courseId) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(assignmentService.getStudentSubmissions(userId, courseId));
    }
}