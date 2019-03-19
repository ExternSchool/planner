package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudyPlanRepositoryTest {
    @Autowired private TestEntityManager entityManager;
    @Autowired private StudyPlanRepository repository;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private List<StudyPlan> expectedStudyPlans;
    private List<SchoolSubject> subjects;

    @Before
    public void setup() {
        expectedStudyPlans = new ArrayList<>();
        subjects = new ArrayList<>();
        for (String name : Arrays.asList("Quantum Mechanics","Algebraic Topology")) {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(name);
            subjects.add(subject);
            entityManager.persist(subject);

            for (GradeLevel level : Arrays.asList(GradeLevel.LEVEL_1, GradeLevel.LEVEL_3)) {
                StudyPlan plan = new StudyPlan(level, subject);
                expectedStudyPlans.add(plan);
            }
        }
    }

    @Test
    public void shouldReturnFourPlans_whenSaveAllAndFindAllPlans() {
        int initialCount = (int)repository.count();
        repository.saveAll(expectedStudyPlans);

        List<StudyPlan> actualStudyPlans = repository.findAll();

        assertThat(actualStudyPlans)
                .containsAll(expectedStudyPlans)
                .size().isEqualTo(initialCount + 4);
    }

    @Test
    public void shouldReturnThreePlans_whenFindBySubjectIdAndGradeLevelAndDeleteThisPlan() {
        repository.saveAll(expectedStudyPlans);
        int initialCount = (int)repository.count();
        repository.deleteAll(repository.findAllByGradeLevelAndSubject(GradeLevel.LEVEL_1, subjects.get(0)));
        List<StudyPlan> actualStudyPlans = repository.findAll();

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isNotEqualTo(expectedStudyPlans.size())
                .isEqualTo(initialCount - 1);
    }

    @Test
    public void shouldReturnTwoPlans_whenFindAllBySubjectIdOrderByGradeLevel() {
        repository.saveAll(expectedStudyPlans);
        List<StudyPlan> actualStudyPlans = repository.findAllBySubjectOrderByGradeLevelAscTitleAsc(subjects.get(0));

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isEqualTo(2);
    }

    @Test
    public void shouldReturnTwoPlans_whenFindAllByGradeLevel() {
        repository.saveAll(expectedStudyPlans);
        List<StudyPlan> actualStudyPlans = repository.findAllByGradeLevelOrderByTitleAsc(GradeLevel.LEVEL_1);

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isEqualTo(2);
    }

    @Test
    @SuppressWarnings({"unchecked", "JpaQlInspection"})
    public void shouldReturnNoPlans_whenNoSubjects() {
        int initialCount = (int)repository.count();
        repository.saveAll(expectedStudyPlans);
        List<StudyPlan> expectedStudyPlans = entityManager
                .getEntityManager()
                .createQuery("Select t from Plan t").getResultList();
        List<StudyPlan> actualStudyPlans = repository.findAllByOrderByGradeLevelAscTitleAsc();

        assertThat(actualStudyPlans)
                .containsAll(expectedStudyPlans)
                .size().isEqualTo(initialCount + 4);

        assertThat(actualStudyPlans.stream().map(StudyPlan::getGradeLevel).collect(Collectors.toList()))
                .containsSequence(GradeLevel.LEVEL_1, GradeLevel.LEVEL_1, GradeLevel.LEVEL_3, GradeLevel.LEVEL_3);
    }
}
