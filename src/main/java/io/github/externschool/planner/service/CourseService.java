package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Teacher;

import java.util.List;

public interface CourseService {
    Course findCourseByStudentIdAndPlanId(Long studentId, Long planId);

    List<Course> findAll();

    List<Course> findAllByStudentId(Long studentId);

    List<Course> findAllByPlanId(Long planId);

    List<Course> findAllByTeacher(Teacher teacher);

    Course saveOrUpdateCourse(Course course);

    void deleteCourse(Course course);
}
