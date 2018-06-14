package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
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
        firstTeacher.setCreatedAt(LocalDateTime.now());
        firstTeacher.setModifiedAt(LocalDateTime.now());

        Teacher secondTeacher = new Teacher("", new HashSet<>());
        secondTeacher.setUser(new User("admin@email.com", "password"));
        secondTeacher.setCreatedAt(LocalDateTime.now());
        secondTeacher.setModifiedAt(LocalDateTime.now());

        entityManager.persist(firstTeacher);
        entityManager.persist(secondTeacher);
        List<Teacher> teachers = this.teacherRepository.findAll();

        assertThat(teachers)
                .isNotNull()
                .hasSize(2)
                .containsSubsequence(firstTeacher, secondTeacher);
    }
}
