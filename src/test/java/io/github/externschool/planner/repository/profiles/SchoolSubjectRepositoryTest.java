package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SchoolSubjectRepositoryTest {

    @Autowired
    private SchoolSubjectRepository schoolSubjectRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldReturnSubjectsOrderedByName(){

        SchoolSubject schoolSubject1 = new SchoolSubject();
        SchoolSubject schoolSubject2 = new SchoolSubject();
        SchoolSubject schoolSubject3 = new SchoolSubject();
        SchoolSubject schoolSubject4 = new SchoolSubject();

        schoolSubject1.setName("history");
        schoolSubject2.setName("math");
        schoolSubject3.setName("biology");
        schoolSubject4.setName("geometry");

        entityManager.persist(schoolSubject1);
        entityManager.persist(schoolSubject2);
        entityManager.persist(schoolSubject3);
        entityManager.persist(schoolSubject4);

        List<SchoolSubject> expectedSubjects = schoolSubjectRepository.findAllByOrderByNameAsc();

        assertThat(expectedSubjects).isNotNull()
                .hasSize(4)
                .containsSequence(schoolSubject3, schoolSubject4, schoolSubject1, schoolSubject2);
    }

}
