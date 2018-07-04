package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.SchoolSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchoolSubjectFormatterTest {
    @Autowired
    private SchoolSubjectFormatter formatter;

    @Test
    public void shouldReturnSameSubjectById_whenRunnedParsePrint() throws ParseException {
        SchoolSubject expectedSubject = new SchoolSubject();
        Long id = 100L;
        expectedSubject.setId(id);
        Locale locale = new Locale("ru");

        SchoolSubject actualSubject = formatter.parse(formatter.print(expectedSubject, locale), locale);

        assertThat(actualSubject)
                .isNotNull()
                .isEqualTo(expectedSubject)
                .hasFieldOrPropertyWithValue("id", id);
    }
}


