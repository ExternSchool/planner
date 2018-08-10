package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository repository;

    @Autowired
    public CourseServiceImpl(final CourseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public Course findCourseByStudentIdAndPlanId(final Long studentId, final Long planId) {
        return repository.findById_StudentIdAndId_PlanId(studentId, planId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByStudentId(final Long studentId) {
        return repository.findAllById_StudentId(studentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByPlanId(final Long planId) {
        return repository.findAllById_PlanId(planId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByTeacher(final Teacher teacher) {
        return repository.findAllByTeacher(teacher);
    }

    @Override
    public Course saveOrUpdateCourse(final Course course) {
        return repository.save(course);
    }

    @Transactional
    @Override
    public void deleteCourse(final Course course) {
        if (repository.findById_StudentIdAndId_PlanId(course.getStudentId(), course.getPlanId()) != null) {
            Optional.ofNullable(course.getTeacher()).ifPresent(teacher -> teacher.removeCourse(course));
            repository.delete(course);
        }
    }
}
