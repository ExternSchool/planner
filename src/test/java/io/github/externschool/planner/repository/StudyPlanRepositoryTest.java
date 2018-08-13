package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
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
public class StudyPlanRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudyPlanRepository repository;

    private List<StudyPlan> expectedStudyPlans;
    private List<SchoolSubject> subjects;

    @Before
    public void setup() {
        expectedStudyPlans = new ArrayList<>();
        subjects = new ArrayList<>();
        for (String name : Arrays.asList("Quantum Mechanics","Algebraic topology")) {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(name);
            subjects.add(subject);
            entityManager.persist(subject);

            for (GradeLevel level : Arrays.asList(GradeLevel.LEVEL_1, GradeLevel.LEVEL_3)) {
                StudyPlan plan = new StudyPlan(level, subject);
                expectedStudyPlans.add(plan);
                entityManager.persist(plan);
            }
        }
    }

    @Test
    public void shouldReturnFourPlans_whenSaveAllAndFindAllPlans() {
        repository.saveAll(expectedStudyPlans);

        List<StudyPlan> actualStudyPlans = repository.findAll();

        assertThat(actualStudyPlans)
                .containsAll(expectedStudyPlans)
                .size().isEqualTo(4);
    }

    @Test
    public void shouldReturnThreePlans_whenFindBySubjectIdAndGradeLevelAndDeleteThisPlan() {
        repository.delete(repository.findByGradeLevelAndSubject(GradeLevel.LEVEL_1, subjects.get(0)));
        List<StudyPlan> actualStudyPlans = repository.findAll();

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isNotEqualTo(expectedStudyPlans.size())
                .isEqualTo(3);
    }

    @Test
    public void shouldReturnTwoPlans_whenFindAllBySubjectIdOrderByGradeLevel() {
        List<StudyPlan> actualStudyPlans = repository.findAllBySubjectOrderByGradeLevel(subjects.get(0));

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isEqualTo(2);
    }

    @Test
    public void shouldReturnTwoPlans_whenFindAllByGradeLevel() {
        List<StudyPlan> actualStudyPlans = repository.findAllByGradeLevelOrderBySubject(GradeLevel.LEVEL_1);

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isEqualTo(2);
    }

    @Test
    @SuppressWarnings({"unchecked", "JpaQlInspection"})
    public void shouldReturnNoPlans_whenNoSubjects() {
        List<StudyPlan> expectedStudyPlans = entityManager
                .getEntityManager()
                .createQuery("Select t from Plan t").getResultList();
        List<StudyPlan> actualStudyPlans = repository.findAllByOrderByGradeLevel();

        assertThat(actualStudyPlans)
                .containsAll(expectedStudyPlans)
                .size().isEqualTo(4);
        assertThat(actualStudyPlans.stream().map(StudyPlan::getGradeLevel).collect(Collectors.toList()))
                .containsExactly(GradeLevel.LEVEL_1, GradeLevel.LEVEL_1, GradeLevel.LEVEL_3, GradeLevel.LEVEL_3);
    }
}
