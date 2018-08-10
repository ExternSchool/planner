package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.CourseRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseServiceTest {
    @Mock private CourseRepository repository;
    @Mock private CourseService courseService;

    private Course expectedCourse;
    private Course anotherCourse;
    private Student expectedStudent;
    private StudyPlan expectedPlan;
    private Teacher teacher;

    @Before
    public void setUp() {
        courseService = new CourseServiceImpl(repository);

        expectedStudent = new Student();
        expectedStudent.setId(1L);
        expectedPlan = new StudyPlan();
        expectedPlan.setId(2L);
        expectedCourse = new Course(expectedStudent.getId(), expectedPlan.getId());

        teacher =  new Teacher(new Person(), "Teacher", new HashSet<>(), new HashSet<>());
        teacher.addCourse(expectedCourse);

        Student student = new Student();
        student.setId(3L);
        StudyPlan plan = new StudyPlan();
        plan.setId(4L);
        anotherCourse = new Course(student.getId(), plan.getId());
    }

    @Test
    public void shouldReturnExpectedCourse_whenFindCourseByStudentIdAndPlanId() {
        Mockito.when(repository.findById_StudentIdAndId_PlanId(expectedStudent.getId(), expectedPlan.getId()))
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
        Mockito.when(repository.findAll())
                .thenReturn(expectedList);

        List<Course> actualCourses = courseService.findAll();

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expectedList);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByStudentId() {
        Mockito.when(repository.findAllById_StudentId(expectedStudent.getId()))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByStudentId(expectedStudent.getId());

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactly(expectedCourse)
                .doesNotContain(anotherCourse);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByPlanId() {
        Mockito.when(repository.findAllById_PlanId(expectedPlan.getId()))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByPlanId(expectedPlan.getId());

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactly(expectedCourse)
                .doesNotContain(anotherCourse);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByTeacher() {
        Mockito.when(repository.findAllByTeacher(teacher))
                .thenReturn(Collections.singletonList(expectedCourse));

        List<Course> actualCourses = courseService.findAllByTeacher(teacher);

        assertThat(actualCourses)
                .isNotEmpty()
                .containsExactly(expectedCourse)
                .doesNotContain(anotherCourse);
    }

    @Test
    public void shouldReturnCourse_whenSaveOrUpdateCourse() {
        Mockito.when(repository.save(expectedCourse))
                .thenReturn(expectedCourse);

        Course actualCourse = courseService.saveOrUpdateCourse(expectedCourse);

        assertThat(actualCourse)
                .isNotNull()
                .isEqualTo(expectedCourse)
                .isEqualToComparingFieldByField(expectedCourse);
    }

    @Test
    public void shouldRemoveCourseFromTeacher_whenDeleteCourse() {
        Mockito.when(repository.findById_StudentIdAndId_PlanId(
                expectedCourse.getStudentId(),
                expectedCourse.getPlanId()))
                .thenReturn(expectedCourse);

        courseService.deleteCourse(expectedCourse);

        verify(repository, times(1)).delete(expectedCourse);
        assertThat(teacher)
                .isNotNull()
                .hasFieldOrProperty("courses")
                .matches(t -> t.getCourses().isEmpty());
    }
}
