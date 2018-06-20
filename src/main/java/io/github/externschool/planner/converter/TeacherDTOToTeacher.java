package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.dto.TeacherDTO;
import org.springframework.core.convert.converter.Converter;

public class TeacherDTOToTeacher implements Converter<TeacherDTO, Teacher> {

    @Override
    public Teacher convert(TeacherDTO teacherDTO) {
        return new Teacher(
                teacherDTO.getId(),
                new User(),
                teacherDTO.getFirstName(),
                teacherDTO.getPatronymicName(),
                teacherDTO.getLastName(),
                teacherDTO.getPhoneNumber(),
                teacherDTO.getVerificationKey(),
                teacherDTO.getOfficer(),
                teacherDTO.getSchoolSubjects()
        );
    }
}
