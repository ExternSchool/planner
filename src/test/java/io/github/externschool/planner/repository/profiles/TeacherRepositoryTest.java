package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Person;
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
        firstTeacher = new Teacher(new Person(), "principal", new HashSet<>(), new HashSet<>());
        firstTeacher.setUser(new User("user@email.com", "password"));
        firstTeacher.setOfficer("Psychologist");
        firstTeacher.setLastName("C");

        secondTeacher = new Teacher(new Person(),"", new HashSet<>(), new HashSet<>());
        secondTeacher.setUser(new User("admin@email.com", "password"));
        secondTeacher.setOfficer("Principal");
        secondTeacher.setLastName("B");

        thirdTeacher = new Teacher(new Person(),"chemist", new HashSet<>(), new HashSet<>());
        thirdTeacher.setUser(new User("chemist@email.com", "password"));
        thirdTeacher.setOfficer("Chemist");
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
        List<Teacher> teachers = this.repository.findAllByOrderByLastNameAsc();

        assertThat(teachers)
                .isNotNull()
                .hasSize(3)
                .containsSubsequence(thirdTeacher, secondTeacher, firstTeacher);
    }

}
