package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

@Component
public class TeacherToTeacherDTO implements Converter<Teacher, TeacherDTO> {

    @Override
    public TeacherDTO convert(Teacher teacher) {
        TeacherDTO teacherDTO = new TeacherDTO();
        BeanUtils.copyProperties(teacher, teacherDTO, "schoolSubjects");
        teacherDTO.setEmail(Optional.ofNullable(teacher.getVerificationKey())
                .map(VerificationKey::getUser)
                .map(User::getEmail)
                .orElse(""));
        teacherDTO.setSchoolSubjects(new HashSet<>(teacher.getSubjects()));

        return teacherDTO;
    }
}
