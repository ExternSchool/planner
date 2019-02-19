package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Teacher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository repository;

    @Autowired
    TestEntityManager entityManager;

    private Teacher firstTeacher;
    private Teacher secondTeacher;
    private Teacher thirdTeacher;

    @Before
    public void setUp() {
        firstTeacher = new Teacher(new Person(), "Psychologist", new HashSet<>(), new HashSet<>());
        firstTeacher.setLastName("C");
        secondTeacher = new Teacher(new Person(), "Principal", new HashSet<>(), new HashSet<>());
        secondTeacher.setLastName("B");
        thirdTeacher = new Teacher(new Person(), "Chemist", new HashSet<>(), new HashSet<>());
        thirdTeacher.setLastName("A");

        entityManager.persist(firstTeacher);
        entityManager.persist(secondTeacher);
        entityManager.persist(thirdTeacher);
    }

    @Test
    public void shouldReturnListOfTeacher() {
        List<Teacher> teachers = this.repository.findAll();

        assertThat(teachers)
                .isNotNull()
                .hasSize(3)
                .containsSubsequence(firstTeacher, secondTeacher, thirdTeacher);
    }

    @Test
    public void shouldReturnTeacherById() {
        Teacher expectedTeacher = this.repository.findTeacherById(secondTeacher.getId());

        assertThat(expectedTeacher)
                .isNotNull()
                .isEqualTo(secondTeacher)
                .isEqualToComparingFieldByField(secondTeacher);
    }

    @Test
    public void shouldReturnSortedListOfTeacher() {
        List<Teacher> teachers = this.repository.findAllByOrderByLastName();

        assertThat(teachers)
                .isNotNull()
                .hasSize(3)
                .containsSubsequence(thirdTeacher, secondTeacher, firstTeacher);
    }

    @Test
    public void shouldReturnListOfTeachers_whenFindAllBySubjectsContains() {
        SchoolSubject subject =  new SchoolSubject();
        entityManager.persist(subject);
        firstTeacher.addSubject(subject);
        secondTeacher.addSubject(subject);
        List<Teacher> teachers = this.repository.findAllBySubjectsContains(subject);

        assertThat(teachers)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(secondTeacher, firstTeacher);
    }

    @Test
    public void shouldReturnSingletonList_whenFindAllByLastName() {
        String expectedName = firstTeacher.getLastName();

        List<Teacher> teachers = this.repository.findAllByLastNameOrderByLastName(expectedName);

        assertThat(teachers)
                .isNotNull()
                .hasSize(1)
                .containsExactly(firstTeacher);
    }
}
