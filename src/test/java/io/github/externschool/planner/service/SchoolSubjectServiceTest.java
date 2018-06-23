package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SchoolSubjectServiceTest {

    @Autowired
    private SchoolSubjectRepository subjectRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldReturnSubjectIfCreated(){
        SchoolSubject schoolSubject = new SchoolSubject();
        schoolSubject.setName("math");
        entityManager.persist(schoolSubject);

        SchoolSubject retrievedSubject = subjectRepository.findByName("math");

        assert(schoolSubject.equals(retrievedSubject));
    }
}
