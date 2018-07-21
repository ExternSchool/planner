package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.course.CourseId;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, CourseId> {

    Course findByCourseId(CourseId courseId);

    Course findByCourseId_PlanId(Long planId);

    List<Course> findAllByCourseId_StudentId(Long studentId);

    List<Course> findAllByTeacher(Teacher teacher);
}