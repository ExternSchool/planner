package io.github.externschool.planner.converter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class LocalDateFormatter implements Formatter<LocalDate> {
    @Override
    public LocalDate parse(final String s, final Locale locale) throws ParseException {
        LocalDate date;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            date = LocalDate.parse(s, formatter);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 1);
        }

        return date;
    }

    @Override
    public String print(final LocalDate date, final Locale locale) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
