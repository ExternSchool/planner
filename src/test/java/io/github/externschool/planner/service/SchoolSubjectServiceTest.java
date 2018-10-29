package io.github.externschool.planner.service;

import io.github.externschool.planner.TestPlannerApplication;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestPlannerApplication.class)
public class SchoolSubjectServiceTest {

    @MockBean
    private SchoolSubjectRepository repository;

    @Autowired
    private SchoolSubjectServiceImpl service;

    private List<SchoolSubject> subjects;
    private List<SchoolSubject> uaTitles;
    private SchoolSubject uaSubject1;
    private SchoolSubject uaSubject2;
    private SchoolSubject uaSubject3;

    private Optional<SchoolSubject> optional;

    @Before
    public void setup() {
        subjects = new ArrayList<>();
        for (long i = 0L; i < 4L; i++) {
            SchoolSubject subject = new SchoolSubject();
            subject.setId(i);
            subject.setTitle(Long.toString(i));
            subjects.add(subject);
        }
        optional = Optional.of(subjects.get(0));

        uaSubject1 = new SchoolSubject();
        uaSubject2 = new SchoolSubject();
        uaSubject3 = new SchoolSubject();

        String title1 = "Астрономія";
        String title2 = "Історія";
        String title3 = "Європейска мова";
        uaSubject1.setTitle(title1);
        uaSubject2 .setTitle(title2);
        uaSubject3.setTitle(title3);

        uaTitles = new ArrayList<>();
        uaTitles.add(uaSubject3);
        uaTitles.add(uaSubject2);
        uaTitles.add(uaSubject1);
    }

    @Test
    public void shouldReturnSubject_whenFindSubjectById() {
        SchoolSubject expected = subjects.get(0);
        Mockito.when(repository.findById(expected.getId()))
                .thenReturn(optional);

        SchoolSubject actual = service.findSubjectById(expected.getId());

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected)
                .isEqualToComparingFieldByField(expected);
    }

    @Test
    public void shouldReturnSubject_whenFindSubjectByName() {
        SchoolSubject expected = subjects.get(0);
        Mockito.when(repository.findByTitle(expected.getTitle()))
                .thenReturn(expected);

        SchoolSubject actual = service.findSubjectByTitle(expected.getTitle());

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected)
                .isEqualToComparingFieldByField(expected);
    }


    @Test
    public void shouldReturnFourSubjects_whenFindAllByOrderByName() {
        Mockito.when(repository.findAllByOrderByTitle())
                .thenReturn(subjects);
        List<SchoolSubject> actual = service.findAllByOrderByTitle();

        assertThat(actual)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(subjects)
                .containsSequence(subjects);
    }

    @Test
    public void shouldReturnTwoSubjects_whenFindAllById() {
        List<Long> indices = Arrays.asList(subjects.get(0).getId(), subjects.get(1).getId());
        List<SchoolSubject> expected = Arrays.asList(subjects.get(0), subjects.get(1));
        Mockito.when(repository.findAllById(indices))
                .thenReturn(expected);
        List<SchoolSubject> actual = service.findAllById(indices);

        assertThat(actual)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expected)
                .hasSize(2);
    }

    @Test
    public void shouldReturnSubject_whenSaveOrUpdateSubject() {
        SchoolSubject expected = subjects.get(0);
        Mockito.when(repository.save(expected))
                .thenReturn(expected);
        Mockito.when(repository.findById(expected.getId()))
                .thenReturn(optional);

        SchoolSubject actual = service.saveOrUpdateSubject(expected);
        assertThat(service.findSubjectById(actual.getId()))
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    public void shouldInvokeOnce_whenDeleteSubject() {
        SchoolSubject deleted = subjects.get(0);
        Mockito.when(repository.findById(deleted.getId()))
                .thenReturn(optional);

        service.deleteSubjectById(deleted.getId());

        verify(repository, times(1)).delete(deleted);
    }

    @Test
    public void shouldSortNonAscii_whenFindAllByOrderByTitle(){
        List<SchoolSubject> expectedList = new ArrayList<>();
        expectedList.add(uaSubject1);
        expectedList.add(uaSubject3);
        expectedList.add(uaSubject2);

        Mockito.when(repository.findAllByOrderByTitle())
                .thenReturn(expectedList);

        List<SchoolSubject> actualList = repository.findAllByOrderByTitle();

        assertThat(actualList).containsSequence(uaSubject1, uaSubject3, uaSubject2);
    }
}
