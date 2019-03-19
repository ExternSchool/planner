package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CourseRepositoryTest {
    @Autowired private CourseRepository repository;
    @Autowired private TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private List<StudyPlan> plans;
    private List<Course> expectedCourses;
    private Student student;
    private Teacher teacher;

    @Before
    public void setup() {
        expectedCourses = new ArrayList<>();
        plans = new ArrayList<>();
        student = new Student(new Person(),
                LocalDate.of(2008, 8, 8),
                Gender.FEMALE,
                "Address",
                GradeLevel.LEVEL_3);
        entityManager.persist(student);

        teacher = new Teacher(new Person(), "Teacher", new HashSet<>(), new HashSet<>());
        entityManager.persist(teacher);

        for (String name : Arrays.asList("Quantum Mechanics", "Algebraic Topology", "Rocket Science")) {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(name);
            entityManager.persist(subject);

            StudyPlan plan = new StudyPlan(student.getGradeLevel(), subject);
            entityManager.persist(plan);
            plans.add(plan);

            Course course = new Course(student.getId(), plan.getId());
            expectedCourses.add(course);

            teacher.addSubject(subject);
            teacher.addCourse(course);
        }
    }

    @Test
    public void shouldReturnThreeCourses_whenSaveAllAndFindAll() {
        int initialCount = (int)repository.count();
        repository.saveAll(expectedCourses);

        List<Course> actualCourses = repository.findAll();

        assertThat(actualCourses)
                .containsAll(expectedCourses)
                .size().isEqualTo(initialCount + 3);
    }

    @Test
    public void shouldReturnTwoCourses_whenFindByStudentIdAndPlanId_thenDeleteThisCourse() {
        int initialCount = (int)repository.count();
        repository.saveAll(expectedCourses);

        repository.delete(repository.findById_StudentIdAndId_PlanId(
                expectedCourses.get(0).getStudentId(),
                expectedCourses.get(0).getPlanId()));
        List<Course> actualCourses = repository.findAll();

        assertThat(actualCourses)
                .containsAnyElementsOf(expectedCourses)
                .size()
                .isNotEqualTo(expectedCourses.size())
                .isEqualTo(initialCount + 2);
    }

    @Test
    public void shouldReturnEmptyList_whenFindAllByStudentId_thenDeleteTheseCourses() {
        int initialCount = (int)repository.count();
        repository.saveAll(expectedCourses);
        List<Course> courses = repository.findAllById_StudentIdOrderByTitle(expectedCourses.get(0).getStudentId());
        repository.deleteAll(courses);
        List<Course> actualCourses = repository.findAll();

        assertThat(actualCourses)
                .doesNotContainAnyElementsOf(expectedCourses)
                .hasSize(initialCount);
    }

    @Test
    public void shouldReturnCourse_whenFindAllByPlanId() {
        repository.saveAll(expectedCourses);

        List<Course> actualCourses = repository.findAllById_PlanIdOrderByTitle(plans.get(0).getId());

        assertThat(actualCourses)
                .containsAnyElementsOf(expectedCourses)
                .size()
                .isEqualTo(1);
    }

    @Test
    public void shouldReturnThreeCourses_whenFindAllByTeacher() {
        repository.saveAll(expectedCourses);

        List<Course> actualCourses = repository.findAllByTeacher_IdOrderByTitle(teacher.getId());

        assertThat(actualCourses)
                .containsAll(expectedCourses)
                .size()
                .isEqualTo(3);
    }

    @Test
    public void shouldReturnSameHash_whenSaveCourse() {
        Long studentId = student.getId();
        long planId = plans.get(0).getId();
        Course course = new Course(studentId, planId);
        int primary = course.hashCode();

        repository.save(course);
        int actual = repository.findById_StudentIdAndId_PlanId(studentId, planId).hashCode();

        assertThat(actual)
                .isEqualTo(primary);
    }
}
