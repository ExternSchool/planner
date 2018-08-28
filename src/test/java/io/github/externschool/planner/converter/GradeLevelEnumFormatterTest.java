package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.GradeLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Locale;

import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_3;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_7;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GradeLevelEnumFormatterTest {
    @Autowired private GradeLevelEnumFormatter formatter;

    @Test
    public void shouldReturnGradeLevel_whenParse() throws ParseException {
        GradeLevel expected = GradeLevel.LEVEL_3;
        Locale locale = new Locale("uk");

        GradeLevel actual = formatter.parse(UK_GRADE_LEVEL_3, locale);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    public void shouldReturnGradeLevel_whenPrint() {
        GradeLevel level = GradeLevel.LEVEL_7;
        Locale locale = new Locale("uk");

        String actual = formatter.print(level, locale);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(UK_GRADE_LEVEL_7);
    }
}
