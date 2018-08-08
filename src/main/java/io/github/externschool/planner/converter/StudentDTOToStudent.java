package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Student;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentDTOToStudent implements Converter<StudentDTO, Student> {

    @Override
    public Student convert(final StudentDTO studentDTO) {
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student, "verificationKey", "email");
        VerificationKey key = studentDTO.getVerificationKey();
        student.addVerificationKey(key);
        if (key != null && key.getUser() != null) {
            key.getUser().addVerificationKey(key);
        }

        return student;
    }
}
