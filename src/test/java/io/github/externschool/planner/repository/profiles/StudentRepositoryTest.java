package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Student;
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
    private StudentRepository studentRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void shouldReturnListOfStudents(){
        Student student1 = new Student();
        student1.setFirstName("Marina");
        student1.setGender(Gender.FEMALE);
        entityManager.persist(student1);

        Student student2 = new Student();
        student2.setFirstName("Vasia");
        student2.setGender(Gender.MALE);
        entityManager.persist(student2);

        List<Student> studentList = this.studentRepository.findAll();

        assertThat(studentList).isNotNull()
                .hasSize(2)
                .containsSubsequence(student1, student2);

        assert(studentList.get(0).equals(student1));
    }

}

