package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseServiceTest {
    @Mock private CourseRepository courseRepository;
    @Mock private StudyPlanRepository planRepository;
    @Mock private TeacherRepository teacherRepository;
    private CourseService courseService;

    private Course expectedCourse;
    private Course anotherCourse;
    private Student expectedStudent;
    private StudyPlan expectedPlan;
    private Teacher teacher;

    @Before
    public void setUp() {
        courseService = new CourseServiceImpl(courseRepository, planRepository, teacherRepository);

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
        Mockito.when(courseRepository.findAllById_StudentIdOrderByTitle(expectedStudent.getId()))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByStudentId(expectedStudent.getId());

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactly(expectedCourse)
                .doesNotContain(anotherCourse);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByPlanId() {
        Mockito.when(courseRepository.findAllById_PlanIdOrderByTitle(expectedPlan.getId()))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByPlanId(expectedPlan.getId());

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactly(expectedCourse)
                .doesNotContain(anotherCourse);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByTeacher() {
        Mockito.when(courseRepository.findAllByTeacher_IdOrderByTitle(teacher.getId()))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByTeacherId(teacher.getId());

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
        String title = courseService.getCourseTitleAndTeacherByCourse(expectedCourse);

        assertThat(title)
                .isNotBlank()
                .contains("Plan 2L - Teacher J.J.");
    }

    @Test
    public void shouldReturnStringNoTitle_whenGetCourseTitleNoTitle() {
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);
        expectedPlan.setTitle("");
        String title = courseService.getCourseTitleAndTeacherByCourse(expectedCourse);

        assertThat(title)
                .isNotBlank()
                .contains(UK_COURSE_NO_TITLE + " - Teacher J.J.");
    }

    @Test
    public void shouldReturnStringNoTeacher_whenGetCourseTitleNoTeacher() {
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);
        expectedCourse.getTeacher().removeCourse(expectedCourse);
        String title = courseService.getCourseTitleAndTeacherByCourse(expectedCourse);

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
        String title = courseService.getCourseTitleAndTeacherByCourse(expectedCourse);

        assertThat(title)
                .isNotBlank()
                .contains(UK_COURSE_NO_TITLE + " - " + UK_COURSE_NO_TEACHER);
    }

    @Test
    public void shouldReturnStudentsCourses_whenFindCoursesForStudent() {
        StudyPlan newPlan = new StudyPlan();
        newPlan.setId(11L);
        Course newCourse = new Course(expectedStudent.getId(), newPlan.getId());
        Teacher noTeacher = new Teacher();
        noTeacher.setLastName(UK_COURSE_NO_TEACHER);
        noTeacher.setId(12L);
        noTeacher.addCourse(newCourse);
        List<Course> expectedCourses = Arrays.asList(expectedCourse, newCourse);
        Mockito.when(planRepository.findAllByGradeLevelOrderByTitleAsc(expectedStudent.getGradeLevel()))
                .thenReturn(Arrays.asList(expectedPlan, newPlan));
        Mockito.when(courseRepository.findAllById_StudentIdOrderByTitle(expectedStudent.getId()))
                .thenReturn(expectedCourses);
        Mockito.when(planRepository.findStudyPlanById(11L))
                .thenReturn(newPlan);
        Mockito.when(planRepository.findStudyPlanById(expectedCourse.getPlanId()))
                .thenReturn(expectedPlan);
        Mockito.when(teacherRepository.findAllByLastNameOrderByLastName(UK_COURSE_NO_TEACHER))
                .thenReturn(Collections.singletonList(noTeacher));

        List<Course> actualCourses = courseService.findCoursesForStudent(expectedStudent);
        actualCourses = courseService.findCoursesForStudent(expectedStudent);

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactlyInAnyOrder(expectedCourse, newCourse)
                .containsExactlyInAnyOrderElementsOf(expectedCourses);

        Mockito.verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void shouldAddOneNewCourse_whenFindCoursesForStudent() {
        StudyPlan newPlan = new StudyPlan();
        newPlan.setId(11L);
        Course newCourse = new Course(expectedStudent.getId(), newPlan.getId());
        Teacher noTeacher = new Teacher();
        noTeacher.setLastName(UK_COURSE_NO_TEACHER);
        noTeacher.setId(12L);
        noTeacher.addCourse(newCourse);
        List<Course> expectedCourses = Arrays.asList(expectedCourse, newCourse);
        Mockito.when(planRepository.findAllByGradeLevelOrderByTitleAsc(expectedStudent.getGradeLevel()))
                .thenReturn(Arrays.asList(expectedPlan, newPlan));
        Mockito.when(courseRepository.findAllById_StudentIdOrderByTitle(expectedStudent.getId()))
                .thenReturn(Collections.singletonList(expectedCourse))
                .thenReturn(expectedCourses);
        Mockito.when(planRepository.findStudyPlanById(11L))
                .thenReturn(newPlan);
        Mockito.when(planRepository.findStudyPlanById(expectedCourse.getPlanId()))
                .thenReturn(expectedPlan);
        Mockito.when(teacherRepository.findAllByLastNameOrderByLastName(UK_COURSE_NO_TEACHER))
                .thenReturn(Collections.singletonList(noTeacher));

        List<Course> actualCourses = courseService.findCoursesForStudent(expectedStudent);
        actualCourses = courseService.findCoursesForStudent(expectedStudent);

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactlyInAnyOrder(expectedCourse, newCourse)
                .containsExactlyInAnyOrderElementsOf(expectedCourses);

        Mockito.verify(courseRepository, times(1)).save(any(Course.class));
    }
}
