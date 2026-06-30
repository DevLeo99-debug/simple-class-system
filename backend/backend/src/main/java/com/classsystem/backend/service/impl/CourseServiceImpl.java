package com.classsystem.backend.service.impl;

import com.classsystem.backend.dto.*;
import com.classsystem.backend.entity.Course;
import com.classsystem.backend.entity.CourseStudent;
import com.classsystem.backend.entity.User;
import com.classsystem.backend.mapper.CourseMapper;
import com.classsystem.backend.mapper.CourseStudentMapper;
import com.classsystem.backend.mapper.UserMapper;
import com.classsystem.backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final UserMapper userMapper;

    @Override
    public Course createCourse(Long teacherId, CourseCreateDTO dto) {
        Course course = new Course();
        course.setName(dto.getName());
        course.setCode(RandomStringUtils.randomAlphanumeric(6).toUpperCase());
        course.setTeacherId(teacherId);
        course.setDescription(dto.getDescription());
        course.setCoverUrl(dto.getCoverUrl());
        course.setStatus("ACTIVE");
        course.setSortOrder(0);
        course.setStudentCount(0);
        courseMapper.insert(course);
        return course;
    }

    @Override
    public Course updateCourse(Long teacherId, CourseUpdateDTO dto) {
        Course course = courseMapper.findById(dto.getId());
        if (course == null || !course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("课程不存在或无权限");
        }
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setCoverUrl(dto.getCoverUrl());
        courseMapper.update(course);
        return course;
    }

    @Override
    public void deleteCourse(Long teacherId, Long courseId) {
        Course course = courseMapper.findById(courseId);
        if (course == null || !course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("课程不存在或无权限");
        }
        courseMapper.deleteById(courseId);
    }

    @Override
    public void archiveCourse(Long teacherId, Long courseId, String archiveType) {
        Course course = courseMapper.findById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        course.setStatus("ARCHIVED");
        course.setArchiveType(archiveType);
        courseMapper.update(course);
    }

    @Override
    public void restoreCourse(Long teacherId, Long courseId) {
        Course course = courseMapper.findById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        course.setStatus("ACTIVE");
        course.setArchiveType(null);
        courseMapper.update(course);
    }

    @Override
    public List<Course> getTeacherCourses(Long teacherId) {
        return courseMapper.findByTeacherId(teacherId);
    }

    @Override
    public List<Course> getArchivedCourses(Long teacherId) {
        return courseMapper.findArchivedByTeacherId(teacherId);
    }

    @Override
    public void sortCourses(Long teacherId, CourseSortDTO dto) {
        for (int i = 0; i < dto.getCourseIds().size(); i++) {
            Course course = courseMapper.findById(dto.getCourseIds().get(i));
            if (course != null && course.getTeacherId().equals(teacherId)) {
                course.setSortOrder(i);
                courseMapper.update(course);
            }
        }
    }

    @Override
    public List<UserInfoDTO> getCourseMembers(Long courseId) {
        List<CourseStudent> courseStudents = courseStudentMapper.findActiveByCourseId(courseId);
        List<UserInfoDTO> result = new ArrayList<>();
        for (CourseStudent cs : courseStudents) {
            User user = userMapper.findById(cs.getStudentId());
            if (user != null) {
                result.add(new UserInfoDTO(
                        user.getId(), user.getUsername(), user.getName(),
                        user.getRole(), user.getEmail(), user.getPhone()
                ));
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void joinCourse(Long studentId, JoinCourseDTO dto) {
        Course course = courseMapper.findByCode(dto.getCode().toUpperCase());
        if (course == null) {
            throw new RuntimeException("加课码无效");
        }
        CourseStudent existing = courseStudentMapper.findActiveRelation(course.getId(), studentId);
        if (existing != null) {
            throw new RuntimeException("你已经加入了该课程");
        }
        CourseStudent cs = new CourseStudent();
        cs.setCourseId(course.getId());
        cs.setStudentId(studentId);
        cs.setStatus("ACTIVE");
        courseStudentMapper.insert(cs);
        courseMapper.incrementStudentCount(course.getId());
    }

    @Override
    @Transactional
    public void withdrawCourse(Long studentId, Long courseId) {
        CourseStudent cs = courseStudentMapper.findActiveRelation(courseId, studentId);
        if (cs == null) {
            throw new RuntimeException("你未加入该课程");
        }
        courseStudentMapper.withdraw(courseId, studentId);
        courseMapper.decrementStudentCount(courseId);
    }

    @Override
    public List<Course> getStudentCourses(Long studentId) {
        List<CourseStudent> courseStudents = courseStudentMapper.findActiveByStudentId(studentId);
        List<Course> result = new ArrayList<>();
        for (CourseStudent cs : courseStudents) {
            Course course = courseMapper.findById(cs.getCourseId());
            if (course != null && "ACTIVE".equals(course.getStatus())) {
                result.add(course);
            }
        }
        return result;
    }
}