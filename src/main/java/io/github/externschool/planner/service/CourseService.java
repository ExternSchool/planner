package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Student;

import java.util.List;

public interface CourseService {
    Course findCourseByStudentIdAndPlanId(Long studentId, Long planId);

    List<Course> findAll();

    List<Course> findAllByStudentId(Long studentId);

    List<Course> findAllByPlanId(Long planId);

    List<Course> findAllByTeacherId(Long teacherId);

    Course saveOrUpdateCourse(Course course);

    void deleteCourse(Course course);

    String getCourseTitleAndTeacherByCourse(Course course);

    List<Course> findCoursesForStudent(Student student);
}
