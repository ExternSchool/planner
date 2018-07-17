package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StudyPlanJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SchoolSubjectRepository repository;

    private List<StudyPlan> expectedStudyPlans;

    @Before
    public void setup() {
        expectedStudyPlans = new ArrayList<>();
        for (String name : Arrays.asList("Quantum Mechanics","Algebraic topology")) {
            SchoolSubject subject = new SchoolSubject();
            subject.setName(name);
            for (GradeLevel level : Arrays.asList(GradeLevel.LEVEL_1, GradeLevel.LEVEL_3)) {
                StudyPlan plan = new StudyPlan(level, subject);
                subject.addStudyPlan(plan);
                expectedStudyPlans.add(plan);
            }

            entityManager.persist(subject);
        }
    }

    @Test
    public void shouldReturnFourPlans_whenAddTwoPlansToEachOfTwoSubjects() {
        List<StudyPlan> actualStudyPlans = repository.findAll().stream()
                .flatMap(o -> o.getStudyPlans().stream())
                .collect(Collectors.toList());

        assertThat(actualStudyPlans)
                .containsAll(expectedStudyPlans)
                .size().isEqualTo(4);
    }

    @Test
    public void shouldReturnTwoPlans_whenDeleteOneSubjectWhichContainsTwoPlans() {
        repository.delete(repository.findByName("Quantum Mechanics"));
        List<StudyPlan> actualStudyPlans = repository.findAll().stream()
                .flatMap(o -> o.getStudyPlans().stream())
                .collect(Collectors.toList());

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isNotEqualTo(expectedStudyPlans.size())
                .isEqualTo(2);
    }

    @Test
    public void shouldReturnThreePlans_whenRemoveStudyPlanFromSubject() {
        SchoolSubject subject = repository.findByName("Quantum Mechanics");
        subject.removeStudyPlan((StudyPlan) subject.getStudyPlans().toArray()[0]);
        entityManager.persist(subject);

        List<StudyPlan> actualStudyPlans = repository.findAll().stream()
                .flatMap(o -> o.getStudyPlans().stream())
                .collect(Collectors.toList());

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isEqualTo(3);
    }

    @Test
    @SuppressWarnings({"unchecked", "JpaQlInspection"})
    public void shouldReturnNoPlans_whenNoSubjects() {
        repository.deleteAll();
        List<StudyPlan> actualStudyPlans = entityManager
                .getEntityManager()
                .createQuery("Select t from Plan t").getResultList();

        assertThat(actualStudyPlans).size().isEqualTo(0);
    }

    @After
    public void tearDown(){
        repository.deleteAll();
    }
}
