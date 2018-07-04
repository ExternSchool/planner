package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.SchoolSubject;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Locale;

@Service
public class SchoolSubjectFormatter implements Formatter<SchoolSubject> {

    @Override
    public SchoolSubject parse(final String s, final Locale locale) throws ParseException {
        SchoolSubject subject = new SchoolSubject();
        try {
            Long id = Long.parseLong(s);
            subject.setId(id);
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage(), 1);
        }

        return subject;
    }

    @Override
    public String print(final SchoolSubject subject, final Locale locale) {
        return subject != null ? subject.getId().toString() : "";
    }
}
