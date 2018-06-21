package io.github.externschool.planner.config;

import io.github.externschool.planner.converter.TeacherDTOToTeacher;
import io.github.externschool.planner.converter.TeacherToTeacherDTO;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new TeacherToTeacherDTO());
        registry.addConverter(new TeacherDTOToTeacher());
    }
}