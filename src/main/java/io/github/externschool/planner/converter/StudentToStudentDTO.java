package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Student;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StudentToStudentDTO implements Converter<Student, StudentDTO> {
    @Override
    public StudentDTO convert(final Student student) {
        StudentDTO studentDTO = new StudentDTO();
        BeanUtils.copyProperties(student, studentDTO);
        studentDTO.setEmail(Optional.ofNullable(student.getVerificationKey().getUser())
                .map(User::getEmail)
                .orElse(""));
        studentDTO.setGradeLevel(student.getGradeLevel() != null ? student.getGradeLevel().getValue() : 0);

        return studentDTO;
    }
}
