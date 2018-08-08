package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Locale;

@Service
public class GenderEnumFormatter implements Formatter<Gender> {
    @Override
    public Gender parse(final String s, final Locale locale) throws ParseException {
        return s.equals("чол.ст.") ? Gender.MALE : Gender.FEMALE;
    }

    @Override
    public String print(final Gender gender, final Locale locale) {
        return gender.toString();
    }
}
