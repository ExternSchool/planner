package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.profile.Student;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.assertj.core.api.AssertionsForClassTypes;
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
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {
    @Autowired private StudentRepository repository;
    @Autowired TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private List<Student> expectedStudents;

    @Before
    public void setUp() {
        Student student1 = new Student();
        student1.setGradeLevel(GradeLevel.LEVEL_1);
        student1.setLastName("C");

        Student student2 = new Student();
        student2.setGradeLevel(GradeLevel.LEVEL_NOT_DEFINED);
        student2.setLastName("B");

        Student student3 = new Student();
        student3.setGradeLevel(GradeLevel.LEVEL_NOT_DEFINED);
        student3.setLastName("A");

        expectedStudents = Arrays.asList(student1, student2, student3);
    }

    @Test
    public void shouldReturnStudent_whenFindById() {
        repository.saveAll(expectedStudents);

        Student expectedStudent = this.repository.findStudentById(expectedStudents.get(0).getId());

        AssertionsForClassTypes.assertThat(expectedStudent)
                .isNotNull()
                .isEqualTo(expectedStudent)
                .isEqualToComparingFieldByField(expectedStudent);
    }

    @Test
    public void shouldReturnListOfStudents_whenFindAll() {
        int initialCount = (int)repository.count();
        repository.saveAll(expectedStudents);

        List<Student> studentList = this.repository.findAll();

        assertThat(studentList)
                .isNotNull()
                .hasSize(initialCount + 3)
                .containsSubsequence(expectedStudents);
    }

    @Test
    public void shouldReturnSortedListOfStudent_whenFindAllByOrderByLastName() {
        int initialCount = (int)repository.count();
        repository.saveAll(expectedStudents);

        List<Student> students = this.repository.findAllByOrderByLastName();

        assertThat(students)
                .isNotNull()
                .hasSize(initialCount + 3)
                .containsSubsequence(expectedStudents.get(2), expectedStudents.get(1), expectedStudents.get(0));
    }

    @Test
    public void shouldReturnSortedListOfStudent_whenFindAllByGradeLevel() {
        int initialCount = repository.findAllByGradeLevelOrderByLastName(GradeLevel.LEVEL_NOT_DEFINED).size();
        repository.saveAll(expectedStudents);

        List<Student> students = repository.findAllByGradeLevelOrderByLastName(GradeLevel.LEVEL_NOT_DEFINED);

        assertThat(students)
                .isNotNull()
                .hasSize(initialCount + 2)
                .containsExactlyInAnyOrder(students.get(1), students.get(0));
    }
}

