package com.example.selectcoursesystem.repository;

import com.example.selectcoursesystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByTeacherTeacherId(Integer teacherId);

    // 热门课程排行榜（面试亮点）
    @Query("SELECT c FROM Course c ORDER BY c.enrolled DESC")
    List<Course> findTopCourses();
}
