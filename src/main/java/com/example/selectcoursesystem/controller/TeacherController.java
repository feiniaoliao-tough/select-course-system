package com.example.selectcoursesystem.controller;

import com.example.selectcoursesystem.entity.Teacher;
import com.example.selectcoursesystem.entity.User;
import com.example.selectcoursesystem.repository.TeacherRepository;
import com.example.selectcoursesystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createTeacher(@RequestBody Map<String, Object> body) {
        try {
            Integer userId = (Integer) body.get("userId");
            String name = (String) body.get("name");
            String department = (String) body.get("department");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setName(name);
            teacher.setDepartment(department);
            return ResponseEntity.ok(teacherRepository.save(teacher));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
