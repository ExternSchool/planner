package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class TeacherToTeacherDTO implements Converter<Teacher, TeacherDTO> {

    @Override
    public TeacherDTO convert(Teacher teacher) {
        TeacherDTO teacherDTO = new TeacherDTO();
        BeanUtils.copyProperties(teacher, teacherDTO, "schoolSubjects");
        if (teacher.getVerificationKey().getUser() != null) {
            teacherDTO.setEmail(teacher.getVerificationKey().getUser().getEmail());
        } else {
            teacherDTO.setEmail("");
        }
        teacherDTO.setSchoolSubjects(new HashSet<>(teacher.getSubjects()));

        return teacherDTO;
    }
}
