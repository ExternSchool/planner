package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.profile.Gender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Locale;

import static io.github.externschool.planner.util.Constants.UK_GENDER_FEMALE;
import static io.github.externschool.planner.util.Constants.UK_GENDER_MALE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GenderEnumFormatterTest {
    @Autowired
    private GenderEnumFormatter formatter;

    @Test
    public void shouldReturnMale_whenParse() throws ParseException {
        Gender expected = Gender.MALE;
        Locale locale = new Locale("uk");

        Gender actual = formatter.parse(UK_GENDER_MALE, locale);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    public void shouldReturnFemale_whenParse() throws ParseException {
        Gender expected = Gender.FEMALE;
        Locale locale = new Locale("uk");

        Gender actual = formatter.parse(UK_GENDER_FEMALE, locale);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    public void shouldReturnMale_whenPrint() {
        Gender value = Gender.MALE;
        Locale locale = new Locale("uk");

        String actual = formatter.print(value, locale);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(UK_GENDER_MALE);
    }
}
