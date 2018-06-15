package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Teacher;
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
    private TeacherRepository teacherRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void shouldReturnListOfPersons(){
        Teacher firstTeacher = new Teacher("principal", new HashSet<>());
        firstTeacher.setUser(new User("user@email.com", "password"));
        firstTeacher.setOfficer("Psychologist");

        Teacher secondTeacher = new Teacher("", new HashSet<>());
        secondTeacher.setUser(new User("admin@email.com", "password"));
        secondTeacher.setOfficer("Principal");

        entityManager.persist(firstTeacher);
        entityManager.persist(secondTeacher);
        List<Teacher> teachers = this.teacherRepository.findAll();

        assertThat(teachers)
                .isNotNull()
                .hasSize(2)
                .containsSubsequence(firstTeacher, secondTeacher);
    }
}
