package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.profile.TeacherDTO;
import org.springframework.core.convert.converter.Converter;

public class TeacherDTOToTeacher implements Converter<TeacherDTO, Teacher> {

    @Override
    public Teacher convert(TeacherDTO teacherDTO) {
        return new Teacher(
                teacherDTO.getPhoneNumber(),
                teacherDTO.getValidationKey(),
                teacherDTO.getFirstName(),
                teacherDTO.getPatronymicName(),
                teacherDTO.getLastName(),
                teacherDTO.getOfficer(),
                teacherDTO.getSubjectList()
        );
    }
}
