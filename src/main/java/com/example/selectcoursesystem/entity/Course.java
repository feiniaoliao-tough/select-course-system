package com.example.selectcoursesystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer capacity = 30;

    @Column(nullable = false)
    private Integer enrolled = 0;

    @Column(length = 100)
    private String schedule;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime createdAt;
}