package io.github.externschool.planner.converter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class LocalTimeFormatter implements Formatter<LocalTime> {
    @Override
    public LocalTime parse(final String s, final Locale locale) throws ParseException {
        LocalTime time;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            time = LocalTime.parse(s, formatter);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 1);
        }

        return time;
    }

    @Override
    public String print(final LocalTime time, final Locale locale) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
