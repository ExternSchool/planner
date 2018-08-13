package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchoolSubjectFormatterTest {
    @Mock private SchoolSubjectRepository repository;
    private SchoolSubjectFormatter formatter;

    private SchoolSubject subject = new SchoolSubject();

    @Before
    public void setup() {
        formatter = new SchoolSubjectFormatter(repository);

        subject.setId(1L);

        Mockito.when(repository.findById(subject.getId()))
                .thenReturn(java.util.Optional.ofNullable(subject));
    }

    @Test
    public void shouldReturnSameSubjectById_whenRunnedParsePrint() throws ParseException {
        Locale locale = new Locale("uk");

        String printed = formatter.print(subject, locale);
        SchoolSubject actualSubject = formatter.parse(printed, locale);

        assertThat(actualSubject)
                .isNotNull()
                .isEqualTo(subject)
                .isEqualToComparingFieldByField(subject);
    }
}


