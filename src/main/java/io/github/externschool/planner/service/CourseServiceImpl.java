package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.repository.profiles.StudentRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TITLE;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudyPlanRepository planRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public CourseServiceImpl(final CourseRepository courseRepository,
                             final StudentRepository studentRepository,
                             final StudyPlanRepository planRepository,
                             final TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.planRepository = planRepository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Course findCourseByStudentIdAndPlanId(final Long studentId, final Long planId) {
        return courseRepository.findById_StudentIdAndId_PlanId(studentId, planId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAll() {
        return  Optional.of(courseRepository.findAll()).orElse(Collections.emptyList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByStudentId(final Long studentId) {
        return Optional.ofNullable(courseRepository.findAllById_StudentIdOrderByTitle(studentId))
                .orElse(Collections.emptyList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByPlanId(final Long planId) {
        return Optional.ofNullable(courseRepository.findAllById_PlanIdOrderByTitle(planId))
                .orElse(Collections.emptyList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllByTeacher(final Teacher teacher) {
        return Optional.ofNullable(courseRepository.findAllByTeacherOrderByTitle(teacher))
                .orElse(Collections.emptyList());
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

    @Transactional(readOnly = true)
    @Override
    public String getCourseTitleAndTeacherByCourse(Course course) {
        return Optional.of(planRepository.findStudyPlanById(course.getPlanId()))
                .filter(Objects::nonNull)
                .map(StudyPlan::getTitle)
                .filter(t -> t != null && !t.equals(""))
                .orElse(UK_COURSE_NO_TITLE)
                + " - "
                + Optional.ofNullable(course.getTeacher())
                .filter(Objects::nonNull)
                .map(Teacher::getShortName)
                .orElse(UK_COURSE_NO_TEACHER);
    }

    @Transactional
    @Override
    public List<Course> selectCoursesForStudent(final Student student) {
        List<StudyPlan> plansToFulfill = planRepository.findAllByGradeLevelOrderByTitleAsc(student.getGradeLevel());
        List<Course> coursesToTake = new ArrayList<>(findAllByStudentId(student.getId()));
        if (plansToFulfill.size() != coursesToTake.size()) {
            List<StudyPlan> plansTaken = coursesToTake.stream()
                    .map(Course::getPlanId)
                    .map(planRepository::findStudyPlanById)
                    .collect(Collectors.toList());
            for (StudyPlan plan : plansToFulfill) {
                if (!plansTaken.contains(plan)) {
                    Course newCourse = new Course(student.getId(), plan.getId());
                    newCourse.setTitle(plan.getTitle());
                    teacherRepository.findAllByLastNameOrderByLastName(UK_COURSE_NO_TEACHER).stream()
                            .findAny()
                            .ifPresent(teacher -> teacher.addCourse(newCourse));
                    coursesToTake.add(newCourse);
                    saveOrUpdateCourse(newCourse);
                }
            }
        }

        return coursesToTake;
    }
}
