package io.github.externschool.planner.config;

import io.github.externschool.planner.converter.CourseDTOToCourse;
import io.github.externschool.planner.converter.CourseToCourseDTO;
import io.github.externschool.planner.converter.GenderEnumFormatter;
import io.github.externschool.planner.converter.GradeLevelEnumFormatter;
import io.github.externschool.planner.converter.LocalDateFormatter;
import io.github.externschool.planner.converter.PersonDTOToPerson;
import io.github.externschool.planner.converter.PersonToPersonDTO;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired private VerificationKeyFormatter keyFormatter;
    @Autowired private SchoolSubjectFormatter subjectFormatter;
    @Autowired private LocalDateFormatter localDateFormatter;
    @Autowired private GenderEnumFormatter genderEnumFormatter;
    @Autowired private GradeLevelEnumFormatter gradeLevelEnumFormatter;

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
        
        registry.addFormatter(keyFormatter);
        registry.addFormatter(subjectFormatter);
        registry.addFormatter(localDateFormatter);
        registry.addFormatter(genderEnumFormatter);
        registry.addFormatter(gradeLevelEnumFormatter);
    }
}
