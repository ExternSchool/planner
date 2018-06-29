package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.SchoolSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SchoolSubjectRepositoryIntegrationTest {
    @Autowired
    private SchoolSubjectRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Before
    public void setUp() {
        List<String> names = new ArrayList<>(Arrays.asList("Mathematics", "English", "Geometry"));
        for (String name : names) {
            SchoolSubject subject = new SchoolSubject();
            subject.setName(name);
            entityManager.persist(subject);
        }
    }

    @Test
    public void shouldReturnPresetSubjects_WhenFindAll() {
        List<String> names = new ArrayList<>(Arrays.asList("Mathematics", "English", "Geometry"));
        List<SchoolSubject> subjects = readSubjects();

        assertThat(subjects)
                .isNotEmpty()
                .size().isEqualTo(3);
        for (SchoolSubject subject: subjects) {
            assertThat(names).contains(subject.getName());
        }
    }

    @Test
    public void shouldAddOneSubject_WhenSaveNew() {
        SchoolSubject subject = new SchoolSubject();
        subject.setName("Algebra");
        repository.save(subject);
        List<SchoolSubject> subjects = readSubjects();

        assertThat(subjects)
                .contains(subject)
                .size().isEqualTo(4);
    }

    @Test
    public void shouldSubtractOneSubject_WhenDelete() {
        SchoolSubject subject = new SchoolSubject();
        subject.setName("Chemistry");
        repository.save(subject);
        repository.delete(subject);
        List<SchoolSubject> subjects = readSubjects();

        assertThat(subjects)
                .doesNotContain(subject)
                .size().isEqualTo(3);
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    private List<SchoolSubject> readSubjects() {
        return new ArrayList<>(repository.findAll());
    }
}
