package com.classsystem.backend.service;

import com.classsystem.backend.dto.*;
import com.classsystem.backend.entity.Course;
import java.util.List;

public interface CourseService {
    Course createCourse(Long teacherId, CourseCreateDTO dto);
    Course updateCourse(Long teacherId, CourseUpdateDTO dto);
    void deleteCourse(Long teacherId, Long courseId);
    void archiveCourse(Long teacherId, Long courseId, String archiveType);
    void restoreCourse(Long teacherId, Long courseId);
    List<Course> getTeacherCourses(Long teacherId);
    List<Course> getArchivedCourses(Long teacherId);
    void sortCourses(Long teacherId, CourseSortDTO dto);
    List<UserInfoDTO> getCourseMembers(Long courseId);
    void joinCourse(Long studentId, JoinCourseDTO dto);
    void withdrawCourse(Long studentId, Long courseId);
    List<Course> getStudentCourses(Long studentId);
}