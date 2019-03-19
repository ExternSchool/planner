package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.repository.StudyPlanRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class StudyPlanServiceTest {
    @Mock private StudyPlanRepository planRepository;
    @Mock private CourseService courseService;
    private StudyPlanService planService;

    private StudyPlan expectedPlan;
    private StudyPlan anotherPlan;
    private SchoolSubject subject;
    private Course expectedCourse;
    private Course anotherCourse;

    @Before
    public void setUp() {
        planService = new StudyPlanServiceImpl(planRepository, courseService);

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
    public void shouldReturnExpectedPlans_whenFindAllBySubjectAndGradeLevel() {
        List<StudyPlan> expectedPlans = Collections.singletonList(expectedPlan);
        Mockito.when(planRepository.findAllByGradeLevelAndSubject(expectedPlan.getGradeLevel(), expectedPlan.getSubject()))
                .thenReturn(expectedPlans);

        List<StudyPlan> studyPlans = planService.findAllByGradeLevelAndSubject(GradeLevel.LEVEL_3, subject);

        assertThat(studyPlans)
                .isNotNull()
                .isEqualTo(expectedPlans)
                .containsExactlyInAnyOrderElementsOf(expectedPlans);
    }

    @Test
    public void shouldReturnExpectedList_whenFindAllBySubjectOrderByGradeLevel() {
        Mockito.when(planRepository.findAllBySubjectOrderByGradeLevelAscTitleAsc(subject))
                .thenReturn(Arrays.asList(expectedPlan, anotherPlan));

        List<StudyPlan> actualPlans = planService.findAllBySubject(subject);

        assertThat(actualPlans)
                .isNotEmpty()
                .containsExactly(expectedPlan, anotherPlan);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByGradeLevelOrderBySubject() {
        Mockito.when(planRepository.findAllByGradeLevelOrderByTitleAsc(expectedPlan.getGradeLevel()))
                .thenReturn(Collections.singletonList(expectedPlan));

        List<StudyPlan> actualPlans = planService.findAllByGradeLevel(expectedPlan.getGradeLevel());

        assertThat(actualPlans)
                .isNotEmpty()
                .contains(expectedPlan);
    }

    @Test
    public void shouldReturnExpectedList_whenFindAllByOrderByGradeLevel() {
        Mockito.when(planRepository.findAllByOrderByGradeLevelAscTitleAsc())
                .thenReturn(Arrays.asList(expectedPlan, anotherPlan));

        List<StudyPlan> actualPlans = planService.findAll();

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
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);
        Mockito.when(courseService.findAllByPlanId(expectedPlan.getId()))
                .thenReturn(Arrays.asList(expectedCourse, anotherCourse));

        planService.deletePlan(expectedPlan);

        verify(planRepository, times(1)).delete(expectedPlan);
        assertThat(subject)
                .isNotNull()
                .hasFieldOrPropertyWithValue("plans", Collections.emptySet());
    }

    @Test
    public void shouldDeleteCourses_whenDeletePlan() {
        Mockito.when(planRepository.findStudyPlanById(expectedPlan.getId()))
                .thenReturn(expectedPlan);
        Mockito.when(courseService.findAllByPlanId(expectedPlan.getId()))
                .thenReturn(Arrays.asList(expectedCourse, anotherCourse));

        planService.deletePlan(expectedPlan);

        verify(planRepository, times(1)).delete(expectedPlan);
    }

    @Test
    public void shouldDoNothing_whenTryingToDeleteNull() {
        planService.deletePlan(null);

        verify(planRepository, never()).delete(null);
    }
}
