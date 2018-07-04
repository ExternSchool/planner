package io.github.externschool.planner.config;

import io.github.externschool.planner.converter.LocalDateFormatter;
import io.github.externschool.planner.converter.SchoolSubjectFormatter;
import io.github.externschool.planner.converter.TeacherDTOToTeacher;
import io.github.externschool.planner.converter.TeacherToTeacherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final SchoolSubjectFormatter subjectFormatter;
    private final LocalDateFormatter localDateFormatter;

    @Autowired
    public WebConfig(final SchoolSubjectFormatter subjectFormatter,
                     final LocalDateFormatter localDateFormatter) {
        this.subjectFormatter = subjectFormatter;
        this.localDateFormatter = localDateFormatter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new TeacherToTeacherDTO());
        registry.addConverter(new TeacherDTOToTeacher());

        registry.addFormatter(subjectFormatter);
        registry.addFormatter(localDateFormatter);
    }
}