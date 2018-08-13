package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SchoolSubjectRepositoryIntegrationTest {
    @Autowired
    private SchoolSubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private EntityManager entityManager;

    private static final List<String> NAMES = Arrays.asList("History", "English", "Geometry");
    private static final List<String> JOBS = Arrays.asList("Historian", "Linguist", "Mathematician");
    private HashMap<SchoolSubject, Teacher> expectedSubjectTeacher;
    private HashMap<Teacher, SchoolSubject> expectedTeacherSubject;

    @Before
    public void setUp() {
        expectedSubjectTeacher = new HashMap<>();
        expectedTeacherSubject = new HashMap<>();
        for (int i = 0; i < NAMES.size(); i++) {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(NAMES.get(i));
            entityManager.persist(subject);

            Teacher teacher = new Teacher();
            teacher.setOfficer(JOBS.get(i));
            teacher.addSubject(subject);
            entityManager.persist(teacher);

            expectedSubjectTeacher.put(subject, teacher);
            expectedTeacherSubject.put(teacher, subject);
        }
    }

    @Test
    public void shouldReturnThreeSubjects_WhenFindAll() {
        List<SchoolSubject> subjects = subjectRepository.findAll();

        assertThat(subjects)
                .isNotEmpty()
                .size().isEqualTo(3);

        subjects.forEach(subject -> assertThat(subject.getTitle())
                .isIn(NAMES));
    }

    @Test
    public void shouldCorrespondSubjectsToTeachers_WhenFindAll() {
        List<SchoolSubject> actualSubjects = subjectRepository.findAll();
        List<Teacher> actualTeachers = teacherRepository.findAll();

        assertThat(actualTeachers)
                .isNotEmpty()
                .size()
                .isEqualTo(3)
                .isEqualTo(actualSubjects.size());
    }

    @Test
    public void shouldContainTeachers_WhenFindAllSubjects() {
        List<SchoolSubject> actualSubjects = subjectRepository.findAll();

        actualSubjects.forEach(subject -> {
            assertThat(subject.getTeachers())
                    .hasSize(1)
                    .containsExactly(expectedSubjectTeacher.get(subject));
        });
    }

    @Test
    public void shouldContainSubjects_WhenFindAllTeachers() {
        List<Teacher> actualTeachers = teacherRepository.findAll();

        actualTeachers.forEach(teacher -> {
            assertThat(teacher.getSubjects())
                    .hasSize(1)
                    .containsExactly(expectedTeacherSubject.get(teacher));
        });
    }

    @Test
    public void shouldAddOneSubject_WhenSaveNew() {
        SchoolSubject subject = new SchoolSubject();
        subject.setTitle("Algebra");
        subjectRepository.save(subject);
        List<SchoolSubject> subjects = subjectRepository.findAll();

        assertThat(subjects)
                .contains(subject)
                .size().isEqualTo(4);
    }

    @Test
    public void shouldSubtractOneSubject_WhenDelete() {
        SchoolSubject subject = new SchoolSubject();
        subject.setTitle("Chemistry");
        subjectRepository.save(subject);
        subjectRepository.delete(subject);
        List<SchoolSubject> subjects = subjectRepository.findAll();

        assertThat(subjects)
                .doesNotContain(subject)
                .size().isEqualTo(3);
    }
}
