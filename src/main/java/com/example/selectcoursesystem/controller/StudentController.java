package com.example.selectcoursesystem.controller;

import com.example.selectcoursesystem.entity.Student;
import com.example.selectcoursesystem.entity.User;
import com.example.selectcoursesystem.repository.StudentRepository;
import com.example.selectcoursesystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createStudent(@RequestBody Map<String, Object> body) {
        try {
            Integer userId = (Integer) body.get("userId");
            String name = (String) body.get("name");
            String className = (String) body.get("className");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Student student = new Student();
            student.setUser(user);
            student.setName(name);
            student.setClassName(className);
            return ResponseEntity.ok(studentRepository.save(student));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}