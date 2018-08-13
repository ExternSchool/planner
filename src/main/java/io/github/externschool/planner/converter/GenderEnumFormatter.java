package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.profile.Gender;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Locale;

import static io.github.externschool.planner.util.Constants.UK_GENDER_MALE;

@Service
public class GenderEnumFormatter implements Formatter<Gender> {
    @Override
    public Gender parse(final String s, final Locale locale) throws ParseException {
        return s.equals(UK_GENDER_MALE) ? Gender.MALE : Gender.FEMALE;
    }

    @Override
    public String print(final Gender gender, final Locale locale) {
        return gender.toString();
    }
}
