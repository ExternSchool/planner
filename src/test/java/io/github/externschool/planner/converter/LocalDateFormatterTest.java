package io.github.externschool.planner.converter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LocalDateFormatterTest {
    @Autowired private LocalDateFormatter formatter;

    @Test
    public void shouldReturnSameDate_whenRunParsePrint() throws ParseException {
        LocalDate expectedDate = LocalDate.of(2018, 7, 4);
        Locale locale = new Locale("uk");

        LocalDate actualDate = formatter.parse(formatter.print(expectedDate, locale), locale);

        assertThat(actualDate)
                .isNotNull()
                .isEqualTo(expectedDate);
    }
}


