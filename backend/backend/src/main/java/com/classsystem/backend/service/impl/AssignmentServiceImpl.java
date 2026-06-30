package com.classsystem.backend.service.impl;

import com.classsystem.backend.dto.AssignmentCreateDTO;
import com.classsystem.backend.dto.ScoreDTO;
import com.classsystem.backend.dto.SubmissionDTO;
import com.classsystem.backend.entity.*;
import com.classsystem.backend.mapper.*;
import com.classsystem.backend.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;
    private final ScoreMapper scoreMapper;
    private final NotificationMapper notificationMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public Assignment createAssignment(Long teacherId, AssignmentCreateDTO dto, MultipartFile file) {
        Course course = courseMapper.findById(dto.getCourseId());
        if (course == null || !course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("课程不存在或无权限");
        }

        Assignment assignment = new Assignment();
        assignment.setCourseId(dto.getCourseId());
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setDeadline(dto.getDeadline());
        assignment.setTotalScore(dto.getTotalScore() != null ? dto.getTotalScore() : 100);
        assignment.setStatus("PUBLISHED");
        // 文件处理暂略
        assignmentMapper.insert(assignment);

        // 发送通知给所有学生
        List<CourseStudent> students = courseStudentMapper.findActiveByCourseId(dto.getCourseId());
        for (CourseStudent cs : students) {
            Notification notification = new Notification();
            notification.setUserId(cs.getStudentId());
            notification.setType("ASSIGNMENT");
            notification.setTitle("新作业发布：" + dto.getTitle());
            notification.setContent("截止时间：" + dto.getDeadline());
            notification.setRelatedId(assignment.getId());
            notification.setIsRead(0);
            notificationMapper.insert(notification);
        }
        return assignment;
    }

    @Override
    public List<Assignment> getCourseAssignments(Long courseId) {
        return assignmentMapper.findByCourseId(courseId);
    }

    @Override
    public List<Submission> getSubmissions(Long assignmentId) {
        return submissionMapper.findByAssignmentId(assignmentId);
    }

    @Override
    @Transactional
    public void gradeSubmission(Long teacherId, ScoreDTO dto) {
        Submission submission = submissionMapper.findById(dto.getSubmissionId());
        if (submission == null) {
            throw new RuntimeException("提交记录不存在");
        }
        Score score = scoreMapper.findBySubmissionId(dto.getSubmissionId());
        if (score == null) {
            score = new Score();
            score.setSubmissionId(dto.getSubmissionId());
            score.setGradedBy(teacherId);
            scoreMapper.insert(score);
        }
        score.setScore(dto.getScore());
        score.setComment(dto.getComment());
        score.setGradedAt(LocalDateTime.now());
        scoreMapper.update(score);
        submissionMapper.markAsGraded(dto.getSubmissionId());
    }

    @Override
    @Transactional
    public void batchGrade(Long teacherId, List<ScoreDTO> scores) {
        for (ScoreDTO dto : scores) {
            gradeSubmission(teacherId, dto);
        }
    }

    @Override
    public void urgeSubmission(Long teacherId, Long assignmentId) {
        Assignment assignment = assignmentMapper.findById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }
        List<CourseStudent> students = courseStudentMapper.findActiveByCourseId(assignment.getCourseId());
        List<Submission> submissions = submissionMapper.findByAssignmentId(assignmentId);
        List<Long> submittedIds = submissions.stream().map(Submission::getStudentId).toList();

        int count = 0;
        for (CourseStudent cs : students) {
            if (!submittedIds.contains(cs.getStudentId())) {
                Notification notification = new Notification();
                notification.setUserId(cs.getStudentId());
                notification.setType("URGE");
                notification.setTitle("作业催交：" + assignment.getTitle());
                notification.setContent("请尽快提交，截止时间：" + assignment.getDeadline());
                notification.setRelatedId(assignmentId);
                notification.setIsRead(0);
                notificationMapper.insert(notification);
                count++;
            }
        }
        if (count == 0) {
            throw new RuntimeException("所有学生已提交");
        }
    }

    @Override
    public void closeAssignment(Long teacherId, Long assignmentId) {
        Assignment assignment = assignmentMapper.findById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }
        assignment.setStatus("CLOSED");
        assignmentMapper.update(assignment);
    }

    @Override
    @Transactional
    public Submission submitAssignment(Long studentId, SubmissionDTO dto, MultipartFile file) {
        Assignment assignment = assignmentMapper.findById(dto.getAssignmentId());
        if (assignment == null || "CLOSED".equals(assignment.getStatus())) {
            throw new RuntimeException("作业不存在或已关闭");
        }
        Submission submission = submissionMapper.findByAssignmentAndStudent(dto.getAssignmentId(), studentId);
        if (submission == null) {
            submission = new Submission();
            submission.setAssignmentId(dto.getAssignmentId());
            submission.setStudentId(studentId);
        }
        submission.setContent(dto.getContent());
        submission.setSubmitTime(LocalDateTime.now());
        submission.setIsLate(LocalDateTime.now().isAfter(assignment.getDeadline()) ? 1 : 0);
        submission.setStatus("SUBMITTED");
        // 文件处理暂略
        if (submission.getId() == null) {
            submissionMapper.insert(submission);
        } else {
            submissionMapper.update(submission);
        }
        return submission;
    }

    @Override
    public List<Submission> getStudentSubmissions(Long studentId, Long courseId) {
        if (courseId != null) {
            List<Assignment> assignments = assignmentMapper.findByCourseId(courseId);
            List<Submission> result = new java.util.ArrayList<>();
            for (Assignment a : assignments) {
                Submission s = submissionMapper.findByAssignmentAndStudent(a.getId(), studentId);
                if (s != null) result.add(s);
            }
            return result;
        }
        return submissionMapper.findByStudentId(studentId);
    }
}