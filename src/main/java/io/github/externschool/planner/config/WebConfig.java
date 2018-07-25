package io.github.externschool.planner.config;

import io.github.externschool.planner.converter.LocalDateFormatter;
import io.github.externschool.planner.converter.PersonDTOToPerson;
import io.github.externschool.planner.converter.PersonToPersonDTO;
import io.github.externschool.planner.converter.SchoolSubjectFormatter;
import io.github.externschool.planner.converter.StudentDTOToStudent;
import io.github.externschool.planner.converter.StudentToStudentDTO;
import io.github.externschool.planner.converter.TeacherDTOToTeacher;
import io.github.externschool.planner.converter.TeacherToTeacherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private SchoolSubjectFormatter subjectFormatter;
    @Autowired
    private LocalDateFormatter localDateFormatter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new TeacherToTeacherDTO());
        registry.addConverter(new TeacherDTOToTeacher());
        registry.addConverter(new PersonToPersonDTO());
        registry.addConverter(new PersonDTOToPerson());

        registry.addConverter(new StudentDTOToStudent());
        registry.addConverter(new StudentToStudentDTO());

        registry.addFormatter(subjectFormatter);
        registry.addFormatter(localDateFormatter);
    }
}
