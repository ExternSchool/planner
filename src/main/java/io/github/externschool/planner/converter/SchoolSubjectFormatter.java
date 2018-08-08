package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.repository.SchoolSubjectRepository;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Locale;

@Service
public class SchoolSubjectFormatter implements Formatter<SchoolSubject> {
    private final SchoolSubjectRepository subjectRepository;

    public SchoolSubjectFormatter(final SchoolSubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public SchoolSubject parse(final String s, final Locale locale) throws ParseException {
        return subjectRepository.findById(Long.parseLong(s)).orElse(null);
    }

    @Override
    public String print(final SchoolSubject subject, final Locale locale) {
        return (subject != null && subject.getId() != null ? subject.getId().toString() : "");
    }
}
