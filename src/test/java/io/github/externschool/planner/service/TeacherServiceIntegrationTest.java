package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.profile.Teacher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TeacherServiceIntegrationTest {
    @Autowired private SchoolSubjectService subjectService;
    @Autowired private TeacherService teacherService;
    @Autowired private StudyPlanService planService;

    private List<SchoolSubject> subjects;
    private List<StudyPlan> plans;
    private List<Teacher> teachers;

    @Before
    public void setUp() {
        subjects = new ArrayList<>();
        plans = new ArrayList<>();
        teachers = new ArrayList<>();
        for (String name : Arrays.asList("Quantum Mechanics", "Algebraic Topology", "Rocket Science")) {
            Teacher teacher = new Teacher();
            teacher.setOfficer("Teacher of " + name);
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(name);
            teacher.addSubject(subject);
            StudyPlan plan = new StudyPlan(GradeLevel.LEVEL_3, subject);
            subject.addPlan(plan);

            subjects.add(subject);
            plans.add(plan);
            teachers.add(teacher);
        }
    }

    @Test
    public void shouldReturnAllTeachers_whenFindAllTeachers() {
        int initialSize = Optional.ofNullable(teacherService.findAllTeachers()).orElse(Collections.emptyList()).size();
        subjects.forEach(subjectService::saveOrUpdateSubject);
        teachers.forEach(teacherService::saveOrUpdateTeacher);

        List<Teacher> actualTeachers = teacherService.findAllTeachers();

        assertThat(actualTeachers)
                .isNotEmpty()
                .hasSize(initialSize + teachers.size())
                .containsAll(teachers);
    }

    @Test
    public void shouldReturnAllSubjects_whenFindAllSubjects() {
        int initialSize = Optional.ofNullable(subjectService.findAllByOrderByTitle())
                .orElse(Collections.emptyList()).size();
        subjects.forEach(subjectService::saveOrUpdateSubject);

        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByTitle();

        assertThat(actualSubjects)
                .isNotEmpty()
                .hasSize(initialSize + subjects.size())
                .containsAll(subjects);
    }

    @Test
    public void shouldRemoveFromTeachers_whenDeleteSubject() {
        int initialSize = Optional.ofNullable(subjectService.findAllByOrderByTitle())
                .orElse(Collections.emptyList()).size();
        subjects.forEach(subjectService::saveOrUpdateSubject);
        teachers.forEach(teacherService::saveOrUpdateTeacher);
        SchoolSubject subjectToRemove = subjects.get(0);
        subjects.remove(subjectToRemove);

        subjectService.deleteSubjectById(subjectToRemove.getId());
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByTitle();
        List<Teacher> actualTeachers = teacherService.findAllTeachers();

        assertThat(actualSubjects)
                .isNotEmpty()
                .containsAll(subjects)
                .hasSize(initialSize + subjects.size());

        actualTeachers.forEach(teacher -> {
            assertThat(teacher.getSubjects())
                    .doesNotContain(subjectToRemove);
        });
    }

    @Test
    public void shouldRemoveFromSubjects_whenDeleteTeacher() {
        subjects.forEach(subjectService::saveOrUpdateSubject);
        teachers.forEach(teacherService::saveOrUpdateTeacher);
        Teacher teacher = teachers.get(0);
        teachers.remove(teacher);

        teacherService.deleteTeacherById(teacher.getId());
        List<Teacher> actualTeachers = teacherService.findAllTeachers();
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByTitle();

        assertThat(actualTeachers)
                .isNotEmpty()
                .containsAll(teachers);

        actualSubjects.forEach(subject -> {
            assertThat(subject.getTeachers())
                    .doesNotContain(teacher);
        });
    }

    @Test
    public void shouldRemovePlans_whenDeleteSubject() {
        int initialSize = Optional.ofNullable(subjectService.findAllByOrderByTitle())
                .orElse(Collections.emptyList()).size();
        subjects.forEach(subjectService::saveOrUpdateSubject);
        plans.forEach(planService::saveOrUpdatePlan);
        SchoolSubject subjectToRemove = subjects.get(0);
        subjects.remove(subjectToRemove);

        subjectService.deleteSubjectById(subjectToRemove.getId());
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByTitle();
        List<StudyPlan> actualPlans = planService.findAll();

        assertThat(actualSubjects)
                .isNotEmpty()
                .containsAll(subjects)
                .hasSize(initialSize + subjects.size());

        actualPlans.forEach(plan -> {
            assertThat(plan.getSubject())
                    .isNotEqualTo(subjectToRemove);
        });
    }

    @After
    public void tearDown() {
        plans.stream().filter(Objects::nonNull).forEach(planService::deletePlan);
        subjects.stream().filter(Objects::nonNull).map(SchoolSubject::getId).forEach(subjectService::deleteSubjectById);
        teachers.stream().filter(Objects::nonNull).map(Teacher::getId).forEach(teacherService::deleteTeacherById);
    }
}
