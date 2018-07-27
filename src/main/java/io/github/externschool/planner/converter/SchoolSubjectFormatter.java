package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.service.SchoolSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Locale;

@Service
public class SchoolSubjectFormatter implements Formatter<SchoolSubject> {
    @Autowired
    private SchoolSubjectService subjectService;

    @Override
    public SchoolSubject parse(final String s, final Locale locale) throws ParseException {
        Long id = Long.parseLong(s);

        return subjectService.findSubjectById(id);
    }

    @Override
    public String print(final SchoolSubject subject, final Locale locale) {
        return subject != null ? subject.getId().toString() : "";
    }
}
