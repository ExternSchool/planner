package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.dto.TeacherDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TeacherToTeacherDTO implements Converter<Teacher, TeacherDTO> {

    @Override
    public TeacherDTO convert(Teacher teacher) {
        return new TeacherDTO(
                teacher.getId(),
                teacher.getVerificationKey(),
                teacher.getFirstName(),
                teacher.getPatronymicName(),
                teacher.getLastName(),
                teacher.getPhoneNumber(),
                teacher.getOfficer(),
                teacher.getSubjects()
        );
    }
}
