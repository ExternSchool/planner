package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TeacherDTOToTeacher implements Converter<TeacherDTO, Teacher> {
    @Override
    public Teacher convert(final TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherDTO, teacher, "verificationKey", "email", "schoolSubjects");
        Optional.ofNullable(teacherDTO.getVerificationKey()).ifPresent(teacher::addVerificationKey);
        teacherDTO.getSchoolSubjects().forEach(teacher::addSubject);

        return teacher;
    }
}
