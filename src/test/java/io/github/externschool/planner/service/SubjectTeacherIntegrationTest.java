package io.github.externschool.planner.service;

import io.github.externschool.planner.TestPlannerApplication;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.StudyPlanRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestPlannerApplication.class)
public class SubjectTeacherIntegrationTest {

    @Autowired
    private SchoolSubjectService subjectService;

    @Autowired
    private TeacherService teacherService;

    //TODO Replace with Service
    @Autowired
    private StudyPlanRepository planRepository;

    private List<SchoolSubject> expectedSubjects;
    private List<Teacher> expectedTeachers;
    private List<StudyPlan> expectedPlans;

    @Before
    public void setUp() {
        expectedSubjects = new ArrayList<>();
        expectedTeachers = new ArrayList<>();
        expectedPlans = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SchoolSubject subject = new SchoolSubject();
            StudyPlan plan = new StudyPlan();
            subject.addPlan(plan);
            subjectService.saveOrUpdateSubject(subject);

            Teacher teacher = new Teacher();
            teacher.addSubject(subject);
            teacherService.saveOrUpdateTeacher(teacher);

            expectedSubjects.add(subject);
            expectedTeachers.add(teacher);
        }
    }

    @Test
    public void shouldReturnTwoSubjects() {
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByName();

        assertThat(actualSubjects)
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyElementsOf(expectedSubjects);
    }

    @Test
    public void shouldReturnTwoTeachers() {
        List<Teacher> actualTeachers = teacherService.findAllTeachers();

        assertThat(actualTeachers)
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyElementsOf(expectedTeachers);
    }

    @Test
    public void shouldRemoveFromTeachers_whenDeleteSubject() {
        SchoolSubject subject = expectedSubjects.get(0);
        expectedSubjects.remove(subject);

        subjectService.deleteSubject(subject.getId());
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByName();
        List<Teacher> actualTeachers = teacherService.findAllTeachers();

        assertThat(actualSubjects)
                .isNotEmpty()
                .containsExactlyElementsOf(expectedSubjects);

        actualTeachers.forEach(teacher -> {
            assertThat(teacher.getSubjects())
                    .doesNotContain(subject);
        });
    }

    @Test
    public void shouldRemoveFromSubjects_whenDeleteTeacher() {
        Teacher teacher = expectedTeachers.get(0);
        expectedTeachers.remove(teacher);

        teacherService.deleteTeacher(teacher.getId());
        List<Teacher> actualTeachers = teacherService.findAllTeachers();
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByName();

        assertThat(actualTeachers)
                .isNotEmpty()
                .containsExactlyElementsOf(expectedTeachers);

        actualSubjects.forEach(subject -> {
            assertThat(subject.getTeachers())
                    .doesNotContain(teacher);
        });
    }

    @After
    public void tearDown() {
        if (!expectedTeachers.isEmpty()) {
            expectedTeachers.forEach(t -> teacherService.deleteTeacher(t.getId()));
        }
        if (!expectedSubjects.isEmpty()) {
            expectedSubjects.forEach(s -> subjectService.deleteSubject(s.getId()));
        }
        planRepository.deleteAll();
    }
}
