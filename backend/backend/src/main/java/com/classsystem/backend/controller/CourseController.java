package com.classsystem.backend.controller;

import com.classsystem.backend.dto.*;
import com.classsystem.backend.entity.Course;
import com.classsystem.backend.result.Result;
import com.classsystem.backend.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/create")
    public Result<Course> createCourse(HttpServletRequest request, @RequestBody CourseCreateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(courseService.createCourse(userId, dto));
    }

    @PutMapping("/update")
    public Result<Course> updateCourse(HttpServletRequest request, @RequestBody CourseUpdateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(courseService.updateCourse(userId, dto));
    }

    @DeleteMapping("/{courseId}")
    public Result<Void> deleteCourse(HttpServletRequest request, @PathVariable Long courseId) {
        Long userId = (Long) request.getAttribute("userId");
        courseService.deleteCourse(userId, courseId);
        return Result.success(null);
    }

    @PutMapping("/archive/{courseId}")
    public Result<Void> archiveCourse(HttpServletRequest request, @PathVariable Long courseId, @RequestParam String archiveType) {
        Long userId = (Long) request.getAttribute("userId");
        courseService.archiveCourse(userId, courseId, archiveType);
        return Result.success(null);
    }

    @PutMapping("/restore/{courseId}")
    public Result<Void> restoreCourse(HttpServletRequest request, @PathVariable Long courseId) {
        Long userId = (Long) request.getAttribute("userId");
        courseService.restoreCourse(userId, courseId);
        return Result.success(null);
    }

    @GetMapping("/my")
    public Result<List<Course>> getMyCourses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(courseService.getTeacherCourses(userId));
    }

    @GetMapping("/archived")
    public Result<List<Course>> getArchivedCourses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(courseService.getArchivedCourses(userId));
    }

    @PutMapping("/sort")
    public Result<Void> sortCourses(HttpServletRequest request, @RequestBody CourseSortDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        courseService.sortCourses(userId, dto);
        return Result.success(null);
    }

    @GetMapping("/{courseId}/members")
    public Result<List<UserInfoDTO>> getCourseMembers(@PathVariable Long courseId) {
        return Result.success(courseService.getCourseMembers(courseId));
    }

    @PostMapping("/join")
    public Result<Void> joinCourse(HttpServletRequest request, @RequestBody JoinCourseDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        courseService.joinCourse(userId, dto);
        return Result.success(null);
    }

    @DeleteMapping("/withdraw/{courseId}")
    public Result<Void> withdrawCourse(HttpServletRequest request, @PathVariable Long courseId) {
        Long userId = (Long) request.getAttribute("userId");
        courseService.withdrawCourse(userId, courseId);
        return Result.success(null);
    }

    @GetMapping("/my-student")
    public Result<List<Course>> getMyStudentCourses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(courseService.getStudentCourses(userId));
    }
}