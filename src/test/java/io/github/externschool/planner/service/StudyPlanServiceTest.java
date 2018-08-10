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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudyPlanServiceTest {
    @Mock private StudyPlanRepository planRepository;
    @Mock private CourseService courseService;
    private StudyPlanService planService;

    private StudyPlan expectedPlan;
    private StudyPlan anotherPlan;
    private SchoolSubject subject;
    private Student expectedStudent;
    private Course expectedCourse;
    private Course anotherCourse;

    @Before
    public void setUp() {
        planService = new StudyPlanServiceImpl(planRepository, courseService);

        subject = new SchoolSubject();
        subject.setId(1L);

        expectedPlan = new StudyPlan(GradeLevel.LEVEL_3, subject);
        expectedPlan.setId(2L);
        expectedStudent = new Student();
        expectedStudent.setId(4L);
        expectedCourse = new Course(expectedStudent.getId(), expectedPlan.getId());

        anotherPlan = new StudyPlan(GradeLevel.LEVEL_7, subject);
        anotherPlan.setId(3L);
        Student student = new Student();
        student.setId(5L);
        anotherCourse = new Course(student.getId(), anotherPlan.getId());
    }

    @Test
    public void shouldReturnExpectedPlan_whenFindById() {

    }

    @Test
    public void shouldReturnExpectedPlan_whenFindBySubjectAndGradeLevel() {

    }

    @Test
    public void shouldReturnExpectedList_whenFindAllBySubjectOrderByGradeLevel() {

    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByGradeLevelOrderBySubject() {

    }

    @Test
    public void shouldReturnExpectedList_whenFindAllByOrderByGradeLevel() {

    }

    @Test
    public void shouldReturnExpectedPlan_whenSaveOrUpdatePlan() {

    }

    @Test
    public void shouldRemovePlanFromSubject_whenDeletePlan() {

    }

    @Test
    public void shouldDeleteCourses_whenDeletePlan() {

    }
}
