package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.profile.Student;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentToStudentDTO implements Converter<Student, StudentDTO> {

    @Override
    public StudentDTO convert(final Student student) {
        StudentDTO studentDTO = new StudentDTO();
        BeanUtils.copyProperties(student, studentDTO);
        if (student.getVerificationKey().getUser() != null) {
            studentDTO.setEmail(student.getVerificationKey().getUser().getEmail());
        } else {
            studentDTO.setEmail("");
        }
        studentDTO.setGradeLevel(student.getGradeLevel().getValue());

        return studentDTO;
    }
}
