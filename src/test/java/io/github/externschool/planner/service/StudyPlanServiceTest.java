package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudyPlanServiceTest {
    @Mock private StudyPlanRepository planRepository;
    @Mock private CourseRepository courseRepository;
    private StudyPlanService planService;

    private StudyPlan expectedPlan;
    private StudyPlan anotherPlan;
    private SchoolSubject subject;
    private Course expectedCourse;
    private Course anotherCourse;

    @Before
    public void setUp() {
        planService = new StudyPlanServiceImpl(planRepository, courseRepository);

        subject = new SchoolSubject();
        subject.setId(1L);

        expectedPlan = new StudyPlan(GradeLevel.LEVEL_3, subject);
        expectedPlan.setId(2L);
        Student studentOne = new Student();
        studentOne.setId(3L);
        expectedCourse = new Course(studentOne.getId(), expectedPlan.getId());
        Student studentTwo = new Student();
        studentTwo.setId(4L);
        anotherCourse = new Course(studentTwo.getId(), expectedPlan.getId());

        anotherPlan = new StudyPlan(GradeLevel.LEVEL_7, subject);
        anotherPlan.setId(5L);
    }

    @Test
    public void shouldReturnExpectedPlan_whenFindById() {
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);

        StudyPlan actualPlan = planService.findById(expectedPlan.getId());

        assertThat(actualPlan)
                .isNotNull()
                .isEqualTo(expectedPlan)
                .isEqualToComparingFieldByField(expectedPlan);
    }

    @Test
    public void shouldReturnExpectedPlan_whenFindBySubjectAndGradeLevel() {
        Mockito.when(planRepository.findByGradeLevelAndSubject(expectedPlan.getGradeLevel(), expectedPlan.getSubject()))
                .thenReturn(expectedPlan);

        StudyPlan actualPlan = planService.findByGradeLevelAndSubject(GradeLevel.LEVEL_3, subject);

        assertThat(actualPlan)
                .isNotNull()
                .isEqualTo(expectedPlan)
                .isEqualToComparingFieldByField(expectedPlan);
    }

    @Test
    public void shouldReturnExpectedList_whenFindAllBySubjectOrderByGradeLevel() {
        Mockito.when(planRepository.findAllBySubjectOrderByGradeLevel(subject))
                .thenReturn(Arrays.asList(expectedPlan, anotherPlan));

        List<StudyPlan> actualPlans = planService.findAllBySubjectOrderByGradeLevel(subject);

        assertThat(actualPlans)
                .isNotEmpty()
                .containsExactly(expectedPlan, anotherPlan);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByGradeLevelOrderBySubject() {
        Mockito.when(planRepository.findAllByGradeLevelOrderBySubject(expectedPlan.getGradeLevel()))
                .thenReturn(Collections.singletonList(expectedPlan));

        List<StudyPlan> actualPlans = planService.findAllByGradeLevelOrderBySubject(expectedPlan.getGradeLevel());

        assertThat(actualPlans)
                .isNotEmpty()
                .contains(expectedPlan);
    }

    @Test
    public void shouldReturnExpectedList_whenFindAllByOrderByGradeLevel() {
        Mockito.when(planRepository.findAllByOrderByGradeLevel())
                .thenReturn(Arrays.asList(expectedPlan, anotherPlan));

        List<StudyPlan> actualPlans = planService.findAllByOrderByGradeLevel();

        assertThat(actualPlans)
                .isNotEmpty()
                .containsExactly(expectedPlan, anotherPlan);
    }

    @Test
    public void shouldReturnExpectedPlan_whenSaveOrUpdatePlan() {
        Mockito.when(planRepository.save(expectedPlan))
                .thenReturn(expectedPlan);

        StudyPlan actualPlan = planService.saveOrUpdatePlan(expectedPlan);

        assertThat(actualPlan)
                .isNotNull()
                .isEqualTo(expectedPlan)
                .isEqualToComparingFieldByField(expectedPlan);
    }

    @Test
    public void shouldRemovePlanFromSubject_whenDeletePlan() {
        Mockito.when(courseRepository.findAllById_PlanId(expectedPlan.getId()))
                .thenReturn(Arrays.asList(expectedCourse, anotherCourse));

        planService.deletePlan(expectedPlan);

        verify(planRepository, times(1)).delete(expectedPlan);
        assertThat(subject)
                .isNotNull()
                .hasFieldOrPropertyWithValue("plans", Collections.emptySet());
    }

    @Test
    public void shouldDeleteCourses_whenDeletePlan() {
        Mockito.when(courseRepository.findAllById_PlanId(expectedPlan.getId()))
                .thenReturn(Arrays.asList(expectedCourse, anotherCourse));

        planService.deletePlan(expectedPlan);

        verify(courseRepository, times(1)).delete(expectedCourse);
        verify(courseRepository, times(1)).delete(anotherCourse);
        verify(planRepository, times(1)).delete(expectedPlan);
    }
}
