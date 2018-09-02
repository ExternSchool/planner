package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Course.CoursePK> {
    Course findById_StudentIdAndId_PlanId(Long studentId, Long planId);

    List<Course> findAllByTeacherOrderByTitle(Teacher teacher);

    List<Course> findAllById_PlanIdOrderByTitle(Long planId);

    List<Course> findAllById_StudentIdOrderByTitle(Long studentId);
}
