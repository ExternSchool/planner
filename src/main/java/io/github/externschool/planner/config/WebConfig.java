package io.github.externschool.planner.config;

import io.github.externschool.planner.converter.CourseDTOToCourse;
import io.github.externschool.planner.converter.CourseFormatter;
import io.github.externschool.planner.converter.CourseToCourseDTO;
import io.github.externschool.planner.converter.GenderEnumFormatter;
import io.github.externschool.planner.converter.GradeLevelEnumFormatter;
import io.github.externschool.planner.converter.LocalDateFormatter;
import io.github.externschool.planner.converter.LocalTimeFormatter;
import io.github.externschool.planner.converter.ParticipantToParticipantDTO;
import io.github.externschool.planner.converter.PersonDTOToPerson;
import io.github.externschool.planner.converter.PersonToPersonDTO;
import io.github.externschool.planner.converter.RoleFormatter;
import io.github.externschool.planner.converter.ScheduleEventToScheduleEventDTO;
import io.github.externschool.planner.converter.ScheduleEventTypeDTOToEventType;
import io.github.externschool.planner.converter.ScheduleEventTypeToDTO;
import io.github.externschool.planner.converter.SchoolSubjectFormatter;
import io.github.externschool.planner.converter.StudentDTOToStudent;
import io.github.externschool.planner.converter.StudentToStudentDTO;
import io.github.externschool.planner.converter.StudyPlanDTOToStudyPlan;
import io.github.externschool.planner.converter.StudyPlanToStudyPlanDTO;
import io.github.externschool.planner.converter.TeacherDTOToTeacher;
import io.github.externschool.planner.converter.TeacherToTeacherDTO;
import io.github.externschool.planner.converter.UserDTOToUser;
import io.github.externschool.planner.converter.UserToUserDTO;
import io.github.externschool.planner.converter.VerificationKeyFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan
public class WebConfig implements WebMvcConfigurer {
    @Autowired private VerificationKeyFormatter keyFormatter;
    @Autowired private SchoolSubjectFormatter subjectFormatter;
    @Autowired private LocalDateFormatter localDateFormatter;
    @Autowired private LocalTimeFormatter localTimeFormatter;
    @Autowired private GenderEnumFormatter genderEnumFormatter;
    @Autowired private GradeLevelEnumFormatter gradeLevelEnumFormatter;
    @Autowired private CourseFormatter courseFormatter;
    @Autowired private RoleFormatter roleFormatter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new TeacherToTeacherDTO());
        registry.addConverter(new TeacherDTOToTeacher());
        registry.addConverter(new PersonToPersonDTO());
        registry.addConverter(new PersonDTOToPerson());
        registry.addConverter(new StudentDTOToStudent());
        registry.addConverter(new StudentToStudentDTO());
        registry.addConverter(new UserDTOToUser());
        registry.addConverter(new UserToUserDTO());
        registry.addConverter(new CourseDTOToCourse());
        registry.addConverter(new CourseToCourseDTO());
        registry.addConverter(new StudyPlanDTOToStudyPlan());
        registry.addConverter(new StudyPlanToStudyPlanDTO());
        registry.addConverter(new ScheduleEventToScheduleEventDTO());
        registry.addConverter(new ScheduleEventTypeToDTO());
        registry.addConverter(new ScheduleEventTypeDTOToEventType());
        registry.addConverter(new ParticipantToParticipantDTO());

        registry.addFormatter(keyFormatter);
        registry.addFormatter(subjectFormatter);
        registry.addFormatter(localDateFormatter);
        registry.addFormatter(localTimeFormatter);
        registry.addFormatter(genderEnumFormatter);
        registry.addFormatter(gradeLevelEnumFormatter);
        registry.addFormatter(courseFormatter);
        registry.addFormatter(roleFormatter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCacheControl(CacheControl.maxAge(30L, TimeUnit.DAYS).cachePublic())
                .resourceChain(true);
    }
}
