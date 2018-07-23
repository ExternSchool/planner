package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Student;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository repository;

    @Autowired
    TestEntityManager entityManager;

    private Student student1;
    private Student student2;
    private Student student3;

    @Before
    public void setUp() {
        student1 = new Student();
        student1.setGender(Gender.FEMALE);
        student1.setGradeLevel(GradeLevel.LEVEL_1);
        student1.setLastName("C");

        student2 = new Student();
        student2.setGender(Gender.MALE);
        student2.setGradeLevel(GradeLevel.LEVEL_2);
        student2.setLastName("B");

        student3 = new Student();
        student3.setGender(Gender.MALE);
        student3.setGradeLevel(GradeLevel.LEVEL_3);
        student3.setLastName("A");

        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.persist(student3);

    }

    @Test
    public void shouldReturnListOfStudents() {

        List<Student> studentList = this.repository.findAll();

        assertThat(studentList)
                .isNotNull()
                .hasSize(3)
                .containsSubsequence(student1, student2, student3);
    }

    @Test
    public void shouldReturnStudentById() {
        Student expectedStudent = this.repository.findStudentById(student2.getId());

        AssertionsForClassTypes.assertThat(expectedStudent)
                .isNotNull()
                .isEqualTo(expectedStudent)
                .isEqualToComparingFieldByField(expectedStudent);
    }

    @Test
    public void shouldReturnSortedListOfStudent_whenFindAllByOrderByLastNameAsc() {
        List<Student> students = this.repository.findAllByOrderByLastNameAsc();

        assertThat(students)
                .isNotNull()
                .hasSize(3)
                .containsSubsequence(student3, student2, student1);
    }
}

