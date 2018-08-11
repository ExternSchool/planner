package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.repository.profiles.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudyPlanRepository planRepository;

    @Autowired
    public CourseServiceImpl(final CourseRepository courseRepository,
                             final StudentRepository studentRepository,
                             final StudyPlanRepository planRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.planRepository = planRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Course findCourseByStudentIdAndPlanId(final Long studentId, final Long planId) {
        return courseRepository.findById_StudentIdAndId_PlanId(studentId, planId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByStudentId(final Long studentId) {
        return courseRepository.findAllById_StudentId(studentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByPlanId(final Long planId) {
        return courseRepository.findAllById_PlanId(planId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByTeacher(final Teacher teacher) {
        return courseRepository.findAllByTeacher(teacher);
    }

    @Override
    public Course saveOrUpdateCourse(final Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    @Override
    public void deleteCourse(final Course course) {
        if (courseRepository.findById_StudentIdAndId_PlanId(course.getStudentId(), course.getPlanId()) != null) {
            Optional.ofNullable(course.getTeacher()).ifPresent(teacher -> teacher.removeCourse(course));
            courseRepository.delete(course);
        }
    }

    @Override
    public void createCoursesForStudent(final Student student) {
        List<StudyPlan> plansToFulfill = planRepository.findAllByGradeLevelOrderBySubject(student.getGradeLevel());
        Set<Course> coursesToTake = new HashSet<>();
        plansToFulfill.forEach(plan -> coursesToTake.add(new Course(student.getId(), plan.getId())));
        coursesToTake.forEach(this::saveOrUpdateCourse);
    }
}
