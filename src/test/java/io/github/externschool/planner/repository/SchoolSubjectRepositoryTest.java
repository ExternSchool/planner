package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.SchoolSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SchoolSubjectRepositoryTest {

    @Autowired
    private SchoolSubjectRepository repository;

    @Autowired
    private EntityManager entityManager;

    private List <SchoolSubject> expectedSubjects;

    @Before
    public void setUp() {
        expectedSubjects = new ArrayList<>();
        Arrays.asList("biology", "geometry", "history", "math").forEach(n -> {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(n);
            expectedSubjects.add(subject);
            entityManager.persist(subject);
        });
    }

    @Test
    public void shouldReturnSchoolSubject_whenFindByName() {
        SchoolSubject expectedSubject = expectedSubjects.get(0);
        String title = expectedSubject.getTitle();

        SchoolSubject actualSubject = repository.findByTitle(title);

        assertThat(actualSubject)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", title);
    }

    @Test
    public void shouldReturnOrderedList_whenFindAllByOrderByNameAsc(){
        List<SchoolSubject> actualSubjects = repository.findAllByOrderByTitle();

        assertThat(actualSubjects)
                .isNotNull()
                .hasSize(4)
                .containsSequence(expectedSubjects);
    }

    @Test
    public void shouldReturnThreeSubjects_whenFindAllById() {
        List<SchoolSubject> threeSubjects = expectedSubjects.subList(0, 3);
        List<Long> indices = threeSubjects.stream()
                .map(SchoolSubject::getId)
                .collect(Collectors.toList());

        List<SchoolSubject> actualSubjects = repository.findAllById(indices);

        assertThat(actualSubjects)
                .isNotEmpty()
                .hasSize(3)
                .containsOnlyElementsOf(threeSubjects)
                .containsAll(threeSubjects);
    }
}
