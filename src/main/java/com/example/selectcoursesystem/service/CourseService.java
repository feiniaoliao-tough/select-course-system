package com.example.selectcoursesystem.service;

import com.example.selectcoursesystem.entity.Course;
import com.example.selectcoursesystem.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> getTopCourses() {
        return courseRepository.findTopCourses();
    }
}
