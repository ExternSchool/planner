package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TeacherDTOToTeacher implements Converter<TeacherDTO, Teacher> {
    @Autowired
    private UserService userService;
    @Autowired
    private TeacherService teacherService;

    @Override
    public Teacher convert(final TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        teacher.setId(teacherDTO.getId());
        teacher.addVerificationKey(teacherDTO.getVerificationKey());
        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setPatronymicName(teacherDTO.getPatronymicName());
        teacher.setLastName(teacherDTO.getLastName());
        teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        teacher.setOfficer(teacherDTO.getOfficer());
        teacher.setSubjects(teacherDTO.getSchoolSubjects());

        return teacher;
    }
}
