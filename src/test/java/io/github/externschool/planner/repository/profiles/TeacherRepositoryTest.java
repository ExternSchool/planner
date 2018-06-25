package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Teacher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
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
        firstTeacher = new Teacher("principal", new HashSet<>());
        firstTeacher.setUser(new User("user@email.com", "password"));
        firstTeacher.setOfficer("Psychologist");
        firstTeacher.setLastName("A");
        firstTeacher.setFirstName("Z");

        secondTeacher = new Teacher("", new HashSet<>());
        secondTeacher.setUser(new User("admin@email.com", "password"));
        secondTeacher.setOfficer("Principal");
        secondTeacher.setLastName("C");
        secondTeacher.setFirstName("A");

        thirdTeacher = new Teacher("chemist", new HashSet<>());
        thirdTeacher.setUser(new User("chemist@email.com", "password"));
        thirdTeacher.setOfficer("Chemist");
        thirdTeacher.setLastName("A");
        thirdTeacher.setFirstName("A");

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
    public void shouldReturnSortedListOfTeacher() {
        List<Teacher> teachers = this.repository.findAllSortByLastNameAndFirstName();

        assertThat(teachers)
                .isNotNull()
                .hasSize(3)
                .containsSubsequence(thirdTeacher, firstTeacher, secondTeacher);
    }

}
