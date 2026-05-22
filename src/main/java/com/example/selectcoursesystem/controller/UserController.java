package com.example.selectcoursesystem.controller;

import com.example.selectcoursesystem.entity.User;
import com.example.selectcoursesystem.repository.StudentRepository;
import com.example.selectcoursesystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StudentRepository studentRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User.Role role = User.Role.valueOf(body.get("role"));
            User user = userService.register(
                    body.get("username"),
                    body.get("password"),
                    body.get("email"),
                    role
            );
            return ResponseEntity.ok(Map.of(
                    "message", "注册成功",
                    "userId", user.getUserId(),
                    "username", user.getUsername(),
                    "role", user.getRole()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        return userService.login(body.get("username"), body.get("password"))
                .map(user -> {
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("message", "登录成功");
                    result.put("userId", user.getUserId());
                    result.put("username", user.getUsername());
                    result.put("role", user.getRole());

                    if (user.getRole() == User.Role.student) {
                        studentRepository.findByUserUserId(user.getUserId())
                                .ifPresent(s -> result.put("studentId", s.getStudentId()));
                    }
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.badRequest().body(Map.of("error", "用户名或密码错误")));
    }
}