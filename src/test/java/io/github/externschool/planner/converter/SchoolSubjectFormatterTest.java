package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.service.SchoolSubjectService;
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
    @Autowired
    private SchoolSubjectService subjectService;

    @Test
    public void shouldReturnSameSubjectById_whenRunnedParsePrint() throws ParseException {
        SchoolSubject expectedSubject = new SchoolSubject();
        subjectService.saveOrUpdateSubject(expectedSubject);
        Long id = expectedSubject.getId();
        Locale locale = new Locale("ru");

        String printed = formatter.print(expectedSubject, locale);
        SchoolSubject actualSubject = formatter.parse(printed, locale);

        assertThat(actualSubject)
                .isNotNull()
                .isEqualTo(expectedSubject)
                .hasFieldOrPropertyWithValue("id", id);
    }
}


