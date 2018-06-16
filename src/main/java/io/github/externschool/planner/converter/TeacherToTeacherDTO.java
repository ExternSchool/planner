package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.profile.TeacherDTO;
import org.springframework.core.convert.converter.Converter;

public class TeacherToTeacherDTO implements Converter<Teacher, TeacherDTO> {

    @Override
    public TeacherDTO convert(Teacher teacher) {
        return new TeacherDTO(
                teacher.getPhoneNumber(),
                teacher.getValidationKey(),
                teacher.getFirstName(),
                teacher.getPatronymicName(),
                teacher.getLastName(),
                teacher.getOfficer(),
                teacher.getSubjectList()
        );
    }
}
