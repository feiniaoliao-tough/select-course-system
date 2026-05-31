package com.example.selectcoursesystem.controller;

import com.example.selectcoursesystem.entity.Course;
import com.example.selectcoursesystem.entity.Teacher;
import com.example.selectcoursesystem.repository.CourseRepository;
import com.example.selectcoursesystem.repository.TeacherRepository;
import com.example.selectcoursesystem.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    @GetMapping("/list")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(courseService.getCourseById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<Course>> getTopCourses() {
        return ResponseEntity.ok(courseService.getTopCourses());
    }

    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @PutMapping("/update/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Integer courseId,
                                          @RequestBody Map<String, Object> body) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("课程不存在"));
            if (body.containsKey("name")) {
                course.setName((String) body.get("name"));
            }
            if (body.containsKey("capacity")) {
                course.setCapacity((Integer) body.get("capacity"));
            }
            if (body.containsKey("schedule")) {
                course.setSchedule((String) body.get("schedule"));
            }
            if (body.containsKey("teacherId")) {
                Teacher teacher = teacherRepository.findById((Integer) body.get("teacherId"))
                        .orElseThrow(() -> new RuntimeException("教师不存在"));
                course.setTeacher(teacher);
            }
            return ResponseEntity.ok(courseRepository.save(course));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
