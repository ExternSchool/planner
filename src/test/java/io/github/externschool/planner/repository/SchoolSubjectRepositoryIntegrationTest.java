package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.SchoolSubject;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SchoolSubjectRepositoryIntegrationTest {
    @Autowired private SchoolSubjectRepository subjectRepository;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private List<SchoolSubject> expectedSubjects;

    @Before
    public void setUp() {
        expectedSubjects = new ArrayList<>();
        Arrays.asList("History", "English", "Geometry").forEach(title -> {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(title);
            expectedSubjects.add(subject);
        });
    }

    @Test
    public void shouldReturnSavedSubjects_WhenFindAll() {
        subjectRepository.saveAll(expectedSubjects);
        List<SchoolSubject> actualSubjects = subjectRepository.findAll();

        assertThat(actualSubjects)
                .isNotEmpty()
                .containsAll(expectedSubjects);
    }

    @Test
    public void shouldSaveOnlyOnce_WhenMultipleSavesOccurred() {
        int initialCount = (int)subjectRepository.count();
        subjectRepository.saveAll(expectedSubjects);
        subjectRepository.saveAll(expectedSubjects);

        List<SchoolSubject> actualSubjects = subjectRepository.findAll();

        assertThat(actualSubjects)
                .isNotEmpty()
                .hasSize(initialCount + expectedSubjects.size());
    }

    @Test
    public void shouldReturnSavedSubject_WhenFindByTitle() {
        SchoolSubject expectedSubject = expectedSubjects.stream().findAny().orElse(null);
        assertThat(expectedSubject)
                .isNotNull();

        subjectRepository.save(expectedSubject);
        SchoolSubject actualSubject = subjectRepository.findByTitle(expectedSubject.getTitle());

        assertThat(actualSubject)
                .isNotNull()
                .isEqualTo(expectedSubject);
    }

    @Test
    public void shouldContainSortedSubjects_WhenFindAllByOrderByTitle() {
        List<SchoolSubject> sortedSubjects = expectedSubjects.stream()
                .sorted(Comparator.comparing(SchoolSubject::getTitle))
                .collect(Collectors.toList());
        subjectRepository.saveAll(expectedSubjects);

        List<SchoolSubject> actualSubjects = subjectRepository.findAllByOrderByTitle();

        assertThat(actualSubjects)
                .isNotNull()
                .containsSequence(sortedSubjects);
    }

    @Test
    public void shouldAddOneSubject_WhenSave() {
        int initialCount = (int)subjectRepository.count();
        SchoolSubject subject = new SchoolSubject();
        subject.setTitle("Algebra");

        subjectRepository.save(subject);
        List<SchoolSubject> subjects = subjectRepository.findAll();

        assertThat(subjects)
                .contains(subject)
                .size().isEqualTo(initialCount + 1);
    }

    @Test
    public void shouldSubtractOneSubject_WhenDelete() {
        int initialCount = (int)subjectRepository.count();
        SchoolSubject subject = new SchoolSubject();
        subject.setTitle("Chemistry");

        subjectRepository.save(subject);
        assertThat(subjectRepository.findAll())
                .hasSize(initialCount + 1);

        subjectRepository.delete(subject);
        List<SchoolSubject> subjects = subjectRepository.findAll();

        assertThat(subjects)
                .doesNotContain(subject)
                .size().isEqualTo(initialCount);
    }
}
