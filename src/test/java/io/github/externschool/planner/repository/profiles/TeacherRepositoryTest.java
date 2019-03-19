package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Person;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeacherRepositoryTest {
    @Autowired private TeacherRepository repository;
    @Autowired TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private List<Teacher> teachers;

    @Before
    public void setUp() {
        Teacher firstTeacher = new Teacher(new Person(), "Psychologist", new HashSet<>(), new HashSet<>());
        firstTeacher.setLastName("C");
        Teacher secondTeacher = new Teacher(new Person(), "Principal", new HashSet<>(), new HashSet<>());
        secondTeacher.setLastName("B");
        Teacher thirdTeacher = new Teacher(new Person(), "Chemist", new HashSet<>(), new HashSet<>());
        thirdTeacher.setLastName("A");

        teachers = Arrays.asList(firstTeacher, secondTeacher, thirdTeacher);
    }

    @Test
    public void shouldReturnListOfTeacher() {
        int initialCount = (int)repository.count();
        repository.saveAll(teachers);
        List<Teacher> teachers = this.repository.findAll();

        assertThat(teachers)
                .isNotNull()
                .hasSize(initialCount + 3)
                .containsSubsequence(teachers);
    }

    @Test
    public void shouldReturnTeacherById() {
        repository.saveAll(teachers);
        Teacher expectedTeacher = teachers.get(1);
        Teacher actualTeacher = repository.findTeacherById(teachers.get(1).getId());

        assertThat(actualTeacher)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedTeacher);
    }

    @Test
    public void shouldReturnSortedListOfTeacher() {
        int initialCount = (int)repository.count();
        repository.saveAll(teachers);
        List<Teacher> actualTeachers = this.repository.findAllByOrderByLastName();

        assertThat(actualTeachers)
                .isNotNull()
                .hasSize(initialCount + 3)
                .containsSubsequence(teachers.get(2), teachers.get(1), teachers.get(0));
    }

    @Test
    public void shouldReturnListOfTeachers_whenFindAllBySubjectsContains() {
        SchoolSubject subject =  new SchoolSubject();
        entityManager.persist(subject);
        teachers.get(0).addSubject(subject);
        teachers.get(1).addSubject(subject);
        repository.saveAll(teachers);

        List<Teacher> actualTeachers = this.repository.findAllBySubjectsContains(subject);

        assertThat(actualTeachers)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(teachers.get(1), teachers.get(0));
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByLastName() {
        String expectedName = teachers.get(0).getLastName();
        repository.saveAll(teachers);

        List<Teacher> actualTeachers = this.repository.findAllByLastNameOrderByLastName(expectedName);

        assertThat(actualTeachers)
                .isNotNull()
                .hasSize(1)
                .containsExactly(teachers.get(0));
    }
}
