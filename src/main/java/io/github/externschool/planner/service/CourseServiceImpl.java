package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_COURSE_ADMIN_IN_CHARGE;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TITLE;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_CONTROL;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final StudyPlanRepository planRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public CourseServiceImpl(final CourseRepository courseRepository,
                             final StudyPlanRepository planRepository,
                             final TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
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
    public List<Course> findAllByTeacherId(final Long teacherId) {
        return Optional.ofNullable(courseRepository.findAllByTeacher_IdOrderByTitle(teacherId))
                .orElse(Collections.emptyList());
    }

    @Override
    public Course saveOrUpdateCourse(final Course course) {
        return courseRepository.save(course);
    }

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
                .map(StudyPlan::getTitle)
                .filter(t -> !t.equals("") && !t.isEmpty())
                .orElse(UK_COURSE_NO_TITLE)
                + " - "
                + Optional.ofNullable(course.getTeacher())
                .map(Teacher::getShortName)
                .orElse(UK_COURSE_NO_TEACHER);
    }

    @Override
    public List<Course> findCoursesForStudent(final Student student) {
        List<Course> courses = new ArrayList<>(findAllByStudentId(student.getId()));
        Set<Long> coursesPlansIds = courses.stream().map(Course::getPlanId).collect(Collectors.toSet());
        Set<Long> supposedPlansIds = planRepository.findAllByGradeLevelOrderByTitleAsc(student.getGradeLevel()).stream()
                .map(StudyPlan::getId)
                .collect(Collectors.toSet());
        if (!coursesPlansIds.equals(supposedPlansIds)) {
            return createCourses(student, courses);
        }

        return courses;
    }

    private List<Course> createCourses(final Student student, List<Course> courses) {
        List<StudyPlan> plansToFulfill = planRepository.findAllByGradeLevelOrderByTitleAsc(student.getGradeLevel());
        if (plansToFulfill.size() != courses.size()) {
            List<StudyPlan> plansTaken = courses.stream()
                    .map(Course::getPlanId)
                    .map(planRepository::findStudyPlanById)
                    .collect(Collectors.toList());
            for (StudyPlan plan : plansToFulfill) {
                if (!plansTaken.contains(plan)) {
                    Course newCourse = new Course(student.getId(), plan.getId());
                    newCourse.setTitle(plan.getTitle());
                    if (plan.getTitle() != null && plan.getTitle().equals(UK_EVENT_TYPE_CONTROL)) {
                        teacherRepository.findAllByLastNameOrderByLastName(UK_COURSE_ADMIN_IN_CHARGE).stream()
                                .findAny()
                                .ifPresent(teacher -> teacher.addCourse(newCourse));
                    } else {
                        teacherRepository.findAllByLastNameOrderByLastName(UK_COURSE_NO_TEACHER).stream()
                                .findAny()
                                .ifPresent(teacher -> teacher.addCourse(newCourse));
                    }
                    courses.add(newCourse);
                    saveOrUpdateCourse(newCourse);
                }
            }
        }

        return courses;
    }
}
