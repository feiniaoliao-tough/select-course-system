package com.example.selectcoursesystem.controller;

import com.example.selectcoursesystem.entity.Enrollment;
import com.example.selectcoursesystem.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<?> enroll(@RequestBody Map<String, Integer> body) {
        try {
            Enrollment enrollment = enrollmentService.enroll(
                    body.get("studentId"),
                    body.get("courseId")
            );
            return ResponseEntity.ok(Map.of(
                    "message", "选课成功",
                    "enrollmentId", enrollment.getEnrollmentId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/drop")
    public ResponseEntity<?> drop(@RequestBody Map<String, Integer> body) {
        try {
            enrollmentService.drop(body.get("studentId"), body.get("courseId"));
            return ResponseEntity.ok(Map.of("message", "退课成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my/{studentId}")
    public ResponseEntity<List<Enrollment>> myEnrollments(@PathVariable Integer studentId) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(studentId));
    }
}
