package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
public class StudyPlanJpaTest {
    @Autowired private TestEntityManager entityManager;
    @Autowired private SchoolSubjectRepository subjectRepository;
    @Autowired private StudyPlanRepository planRepository;

    private List<StudyPlan> expectedStudyPlans;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        expectedStudyPlans = new ArrayList<>();
        for (String name : Arrays.asList("Quantum Mechanics","Algebraic Topology")) {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(name);
            for (GradeLevel level : Arrays.asList(GradeLevel.LEVEL_1, GradeLevel.LEVEL_3)) {
                StudyPlan plan = new StudyPlan(level, subject);
                plan.setSubject(subject);
                expectedStudyPlans.add(plan);
                entityManager.persist(plan);
            }

            entityManager.persist(subject);
        }
    }

    @Test
    public void shouldReturnFourPlans_whenAddTwoPlansToEachOfTwoSubjects() {
        List<StudyPlan> actualStudyPlans = subjectRepository.findAll().stream()
                .flatMap(o -> o.getPlans().stream())
                .collect(Collectors.toList());

        assertThat(actualStudyPlans)
                .containsAll(expectedStudyPlans)
                .size().isEqualTo(4);
    }

    @Test
    public void shouldReturnThreePlans_whenRemoveStudyPlanFromSubject() {
        SchoolSubject subject = subjectRepository.findByTitle("Quantum Mechanics");
        subject.getPlans().stream().findFirst().ifPresent(StudyPlan::removeSubject);

        entityManager.persist(subject);

        List<StudyPlan> actualStudyPlans = subjectRepository.findAll().stream()
                .flatMap(o -> o.getPlans().stream())
                .collect(Collectors.toList());

        assertThat(actualStudyPlans)
                .containsAnyElementsOf(expectedStudyPlans)
                .size()
                .isEqualTo(3);
    }


    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    public void shouldThrowDataIntegrityViolationException_whenDeleteSubjectWhichContainsPlans() {
        subjectRepository.deleteAll();
        planRepository.findAll();
    }
}
