package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TeacherDTOToTeacher implements Converter<TeacherDTO, Teacher> {

    @Override
    public Teacher convert(final TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        teacher.setId(teacherDTO.getId());
        teacher.getVerificationKey().setValue(teacherDTO.getVerificationKeyValue());
        teacher.getVerificationKey().setPerson(teacher);
        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setPatronymicName(teacherDTO.getPatronymicName());
        teacher.setLastName(teacherDTO.getLastName());
        teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        teacher.setOfficer(teacherDTO.getOfficer());
        teacher.setSubjects(teacherDTO.getSchoolSubjects());

        return teacher;
    }
}
