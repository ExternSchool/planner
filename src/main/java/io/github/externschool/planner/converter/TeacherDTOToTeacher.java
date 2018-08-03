package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeacherDTOToTeacher implements Converter<TeacherDTO, Teacher> {
    @Autowired private VerificationKeyService keyService;

    @Override
    public Teacher convert(final TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        teacher.setId(teacherDTO.getId());
        VerificationKey key = keyService.findKeyByValue(teacherDTO.getVerificationKeyValue());
        teacher.addVerificationKey(key);
        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setPatronymicName(teacherDTO.getPatronymicName());
        teacher.setLastName(teacherDTO.getLastName());
        teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        teacher.setOfficer(teacherDTO.getOfficer());
        teacher.setSubjects(teacherDTO.getSchoolSubjects());

        return teacher;
    }
}
