package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.repository.profiles.StudentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseServiceTest {
    @Mock private CourseRepository courseRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private StudyPlanRepository planRepository;
    private CourseService courseService;

    private Course expectedCourse;
    private Course anotherCourse;
    private Student expectedStudent;
    private StudyPlan expectedPlan;
    private Teacher teacher;

    @Before
    public void setUp() {
        courseService = new CourseServiceImpl(courseRepository, studentRepository, planRepository);

        expectedStudent = new Student();
        expectedStudent.setId(1L);
        expectedPlan = new StudyPlan();
        expectedPlan.setId(2L);
        expectedPlan.setTitle("Plan 2L");
        expectedCourse = new Course(expectedStudent.getId(), expectedPlan.getId());

        teacher =  new Teacher(new Person(), "Teacher", new HashSet<>(), new HashSet<>());
        teacher.setId(7L);
        teacher.setLastName("Teacher");
        teacher.setFirstName("John");
        teacher.setPatronymicName("Jacob");
        teacher.addCourse(expectedCourse);

        Student student = new Student();
        student.setId(3L);
        StudyPlan plan = new StudyPlan();
        plan.setId(4L);
        anotherCourse = new Course(student.getId(), plan.getId());
    }

    @Test
    public void shouldReturnExpectedCourse_whenFindCourseByStudentIdAndPlanId() {
        Mockito.when(courseRepository.findById_StudentIdAndId_PlanId(expectedStudent.getId(), expectedPlan.getId()))
                .thenReturn(expectedCourse);

        Course actualCourse = courseService
                .findCourseByStudentIdAndPlanId(expectedStudent.getId(), expectedPlan.getId());

        assertThat(actualCourse)
                .isNotNull()
                .isEqualTo(expectedCourse)
                .hasFieldOrPropertyWithValue("teacher", expectedCourse.getTeacher())
                .matches(course -> course.getStudentId().equals(expectedCourse.getStudentId()))
                .matches(course -> course.getPlanId().equals(expectedCourse.getPlanId()));
    }

    @Test
    public void shouldReturnExpectedList_whenFindAll() {
        List<Course> expectedList = Arrays.asList(expectedCourse, anotherCourse);
        Mockito.when(courseRepository.findAll())
                .thenReturn(expectedList);

        List<Course> actualCourses = courseService.findAll();

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expectedList);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByStudentId() {
        Mockito.when(courseRepository.findAllById_StudentId(expectedStudent.getId()))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByStudentId(expectedStudent.getId());

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactly(expectedCourse)
                .doesNotContain(anotherCourse);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByPlanId() {
        Mockito.when(courseRepository.findAllById_PlanId(expectedPlan.getId()))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByPlanId(expectedPlan.getId());

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactly(expectedCourse)
                .doesNotContain(anotherCourse);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByTeacher() {
        Mockito.when(courseRepository.findAllByTeacher(teacher))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByTeacher(teacher);

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactly(expectedCourse)
                .doesNotContain(anotherCourse);
    }

    @Test
    public void shouldReturnCourse_whenSaveOrUpdateCourse() {
        Mockito.when(courseRepository.save(expectedCourse))
                .thenReturn(expectedCourse);

        Course actualCourse = courseService.saveOrUpdateCourse(expectedCourse);

        assertThat(actualCourse)
                .isNotNull()
                .isEqualTo(expectedCourse)
                .isEqualToComparingFieldByField(expectedCourse);
    }

    @Test
    public void shouldRemoveCourseFromTeacher_whenDeleteCourse() {
        Mockito.when(courseRepository.findById_StudentIdAndId_PlanId(
                expectedCourse.getStudentId(),
                expectedCourse.getPlanId()))
                .thenReturn(expectedCourse);

        courseService.deleteCourse(expectedCourse);

        verify(courseRepository, times(1)).delete(expectedCourse);
        assertThat(teacher)
                .isNotNull()
                .hasFieldOrProperty("courses")
                .matches(t -> t.getCourses().isEmpty());
    }

    @Test
    public void shouldReturnString_whenGetCourseTitleByCourse() {
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);
        String title = courseService.getCourseTitleByCourse(expectedCourse);

        assertThat(title)
                .isNotBlank()
                .contains("Plan 2L - Teacher J.J.");
    }

    @Test
    public void shouldReturnStringNoTitle_whenGetCourseTitleNoTitle() {
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);
        expectedPlan.setTitle("");
        String title = courseService.getCourseTitleByCourse(expectedCourse);

        assertThat(title)
                .isNotBlank()
                .contains(UK_COURSE_NO_TITLE + " - Teacher J.J.");
    }

    @Test
    public void shouldReturnStringNoTeacher_whenGetCourseTitleNoTeacher() {
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);
        expectedCourse.getTeacher().removeCourse(expectedCourse);
        String title = courseService.getCourseTitleByCourse(expectedCourse);

        assertThat(title)
                .isNotBlank()
                .contains("Plan 2L - " + UK_COURSE_NO_TEACHER);
    }

    @Test
    public void shouldReturnStringNoTitleNoTeacher_whenGetCourseTitleNoTitleNoTeacher() {
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);
        expectedCourse.getTeacher().removeCourse(expectedCourse);
        expectedPlan.setTitle(null);
        String title = courseService.getCourseTitleByCourse(expectedCourse);

        assertThat(title)
                .isNotBlank()
                .contains(UK_COURSE_NO_TITLE + " - " + UK_COURSE_NO_TEACHER);
    }

    @Test
    public void shouldCreateCoursesForStudent_whenCreateCoursesForStudent() {
        StudyPlan newPlan = new StudyPlan();
        newPlan.setId(11L);
        Course newCourse = new Course(expectedStudent.getId(), newPlan.getId());
        expectedCourse.getTeacher().removeCourse(expectedCourse);
        Mockito.when(planRepository.findAllByGradeLevelOrderBySubject(expectedStudent.getGradeLevel()))
                .thenReturn(Arrays.asList(expectedPlan, newPlan));
        List<Course> expectedCourses = Arrays.asList(expectedCourse, newCourse);

        List<Course> actualCourses = courseService.createCoursesForStudent(expectedStudent);

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactlyInAnyOrder(expectedCourse, newCourse)
                .containsExactlyInAnyOrderElementsOf(expectedCourses);
    }
}
