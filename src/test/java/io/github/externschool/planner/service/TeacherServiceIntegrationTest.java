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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Transactional
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
            plan.setSubject(subject);

            subjects.add(subject);
            plans.add(plan);
            teachers.add(teacher);

            subjectService.saveOrUpdateSubject(subject);
            planService.saveOrUpdatePlan(plan);
            teacherService.saveOrUpdateTeacher(teacher);
        }
    }

    @Test
    public void shouldReturnSameTeachers_whenUpdateTeachers() {
        List<Teacher> allTeachers = teacherService.findAllTeachers();
        int initialSize = allTeachers.size();

        teachers.forEach(teacherService::saveOrUpdateTeacher);
        List<Teacher> actualTeachers = teacherService.findAllTeachers();

        assertThat(actualTeachers)
                .isNotEmpty()
                .hasSize(initialSize)
                .containsExactlyInAnyOrderElementsOf(allTeachers);
    }

    @Test
    public void shouldReturnSameSubjects_whenUpdateSubjects() {
        List<SchoolSubject> allSubjects = subjectService.findAllByOrderByTitle();
        int initialSize = allSubjects.size();

        subjects.forEach(subjectService::saveOrUpdateSubject);
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByTitle();

        assertThat(actualSubjects)
                .isNotEmpty()
                .hasSize(initialSize)
                .containsExactlyInAnyOrderElementsOf(allSubjects);
    }

    @Test
    public void shouldRemoveFromTeachers_whenDeleteSubject() {
        List<SchoolSubject> allSubjects = subjectService.findAllByOrderByTitle();
        int initialSize = allSubjects.size();
        subjects.forEach(subjectService::saveOrUpdateSubject);
        teachers.forEach(teacherService::saveOrUpdateTeacher);
        SchoolSubject subjectToRemove = subjects.get(0);
        allSubjects.remove(subjectToRemove);

        subjectService.deleteSubjectById(subjectToRemove.getId());
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByTitle();
        List<Teacher> actualTeachers = teacherService.findAllTeachers();

        assertThat(actualSubjects)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(allSubjects)
                .hasSize(initialSize - 1);

        actualTeachers.forEach(teacher ->
            assertThat(teacher.getSubjects())
                    .doesNotContain(subjectToRemove));
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

        actualSubjects.forEach(subject ->
            assertThat(subject.getTeachers())
                    .doesNotContain(teacher));
    }

    @Test
    public void shouldRemovePlans_whenDeleteSubject() {
        List<SchoolSubject> allSubjects = subjectService.findAllByOrderByTitle();
        List<Teacher> allTeachers = teacherService.findAllTeachers();
        List<StudyPlan> allPlans = planService.findAll();
        int initialSize = allSubjects.size();
        SchoolSubject subjectToRemove = allSubjects.get(0);
        allSubjects.remove(subjectToRemove);
        Set<StudyPlan> plansToRemove = subjectToRemove.getPlans();

        subjectService.deleteSubjectById(subjectToRemove.getId());
        List<SchoolSubject> actualSubjects = subjectService.findAllByOrderByTitle();
        List<StudyPlan> actualPlans = planService.findAll();

        assertThat(actualSubjects)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(allSubjects)
                .hasSize(initialSize - 1);

        actualPlans.forEach(plan ->
            assertThat(plan.getSubject())
                    .isNotEqualTo(subjectToRemove));

        assertThat(actualPlans)
                .doesNotContainAnyElementsOf(plansToRemove);
    }

    @After
    public void tearDown() {
        plans.forEach(planService::deletePlan);
        subjects.stream().map(SchoolSubject::getId).forEach(subjectService::deleteSubjectById);
        teachers.stream().map(Teacher::getId).forEach(teacherService::deleteTeacherById);
    }
}
