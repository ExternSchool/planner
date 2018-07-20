package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.core.convert.converter.Converter;

public class TeacherToTeacherDTO implements Converter<Teacher, TeacherDTO> {

    @Override
    public TeacherDTO convert(Teacher teacher) {
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(teacher.getId());
        teacherDTO.setVerificationKeyValue(teacher.getVerificationKey().getValue());
        teacherDTO.setFirstName(teacher.getFirstName());
        teacherDTO.setPatronymicName(teacher.getPatronymicName());
        teacherDTO.setLastName(teacher.getLastName());
        teacherDTO.setPhoneNumber(teacher.getPhoneNumber());
        teacherDTO.setOfficer(teacher.getOfficer());
        teacherDTO.setSchoolSubjects(teacher.getSubjects());

        return teacherDTO;
    }
}
