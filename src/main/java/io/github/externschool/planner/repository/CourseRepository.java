package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Course.CoursePK> {
    Course findById_StudentIdAndId_PlanId(Long studentId, Long planId);

    List<Course> findAllByTeacher(Teacher teacher);

    List<Course> findAllById_PlanId(Long planId);

    List<Course> findAllById_StudentId(Long studentId);

    Course findById(long id);
}
