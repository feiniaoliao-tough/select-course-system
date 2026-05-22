package com.example.selectcoursesystem.service;

import com.example.selectcoursesystem.entity.Course;
import com.example.selectcoursesystem.entity.Enrollment;
import com.example.selectcoursesystem.entity.Student;
import com.example.selectcoursesystem.repository.CourseRepository;
import com.example.selectcoursesystem.repository.EnrollmentRepository;
import com.example.selectcoursesystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    // 选课（核心：事务 + 并发控制）
    @Transactional
    public Enrollment enroll(Integer studentId, Integer courseId) {
        if (enrollmentRepository.existsByStudentStudentIdAndCourseCourseId(studentId, courseId)) {
            throw new RuntimeException("已经选过这门课了");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        if (course.getEnrolled() >= course.getCapacity()) {
            throw new RuntimeException("课程已满");
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        // 更新选课人数
        course.setEnrolled(course.getEnrolled() + 1);
        courseRepository.save(course);

        // 创建选课记录
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus(Enrollment.Status.active);
        return enrollmentRepository.save(enrollment);
    }

    // 退课
    @Transactional
    public void drop(Integer studentId, Integer courseId) {
        Enrollment enrollment = enrollmentRepository
                .findByStudentStudentIdAndCourseCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("未找到选课记录"));

        enrollment.setStatus(Enrollment.Status.dropped);
        enrollmentRepository.save(enrollment);

        Course course = enrollment.getCourse();
        course.setEnrolled(course.getEnrolled() - 1);
        courseRepository.save(course);
    }

    public List<Enrollment> getMyEnrollments(Integer studentId) {
        return enrollmentRepository.findByStudentStudentId(studentId);
    }
}
