package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentServiceIntegrationTest {
    @Autowired private TeacherService teacherService;
    @Autowired private CourseService courseService;
    @Autowired private StudentService studentService;
    @Autowired private StudyPlanService planService;
    @Autowired private SchoolSubjectService subjectService;
    @Autowired private VerificationKeyService keyService;

    private Teacher teacher;
    private Student student;
    private List<SchoolSubject> subjects;
    private List<StudyPlan> plans;
    private List<Course> courses;

    @Before
    public void setup() {
        GradeLevel gradeLevel = GradeLevel.LEVEL_3;

        VerificationKey teacherKey = keyService.saveOrUpdateKey(new VerificationKey());
        VerificationKey studentKey = keyService.saveOrUpdateKey(new VerificationKey());
        teacher = new Teacher(101L,
                "Tfirst",
                "Tpatron",
                "Tlast",
                "(000)000-0000",
                teacherKey,
                "Test Teacher",
                new HashSet<>(),
                new HashSet<>());
        student = new Student(102L,
                "Sfirst",
                "Spatron",
                "Slast",
                "(000)000-0000",
                studentKey,
                LocalDate.of(2010, 1, 1),
                Gender.FEMALE,
                "Address 123",
                gradeLevel);
        subjects = new ArrayList<>();
        plans = new ArrayList<>();
        courses = new ArrayList<>();

        for (String name : Arrays.asList("Quantum Mechanics", "Algebraic Topology", "Rocket Science")) {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(name);
            subjects.add(subject);
            subjectService.saveOrUpdateSubject(subject);

            StudyPlan plan = new StudyPlan(student.getGradeLevel(), subject);
            plans.add(plan);
            planService.saveOrUpdatePlan(plan);

            Course course = new Course(student.getId(), plan.getId());
            courses.add(course);
            courseService.saveOrUpdateCourse(course);

            teacher.addSubject(subject);
            teacher.addCourse(course);
        }
        teacherService.saveOrUpdateTeacher(teacher);
        studentService.saveOrUpdateStudent(student);
    }

    @Test
    public void shouldRemainAllCoursesWithoutTeacher_whenDeleteTeacher() {
        List<Course> expectedCourses = courseService.findAllByStudentId(student.getId());

        teacherService.deleteTeacherById(teacher.getId());
        List<Course> actualCourses = courseService.findAllByStudentId(student.getId());

        assertThat(actualCourses)
                .containsAll(expectedCourses)
                .size().isEqualTo(expectedCourses.size());

        actualCourses.stream().map(Course::getTeacher).forEach(t ->
                assertThat(t)
                        .isNull());
    }
}
