package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.GradeLevel;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

@Service
public class GradeLevelEnumFormatter implements Formatter<GradeLevel> {
    @Override
    public GradeLevel parse(final String s, final Locale locale) throws ParseException {
        return Arrays.stream(GradeLevel.values())
                .filter(gradeLevel -> gradeLevel.toString().equals(s))
                .findAny()
                .orElse(GradeLevel.LEVEL_NOT_DEFINED);
    }

    @Override
    public String print(final GradeLevel gradeLevel, final Locale locale) {
        return gradeLevel.toString();
    }
}
