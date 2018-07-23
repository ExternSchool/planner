package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.profile.Student;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentDTOToStudent implements Converter<StudentDTO, Student> {

    @Override
    public Student convert(final StudentDTO studentDTO) {
        Student student = new Student();
        student.setId(studentDTO.getId());
        student.addVerificationKey(studentDTO.getVerificationKey());
        student.setFirstName(studentDTO.getFirstName());
        student.setPatronymicName(studentDTO.getPatronymicName());
        student.setLastName(studentDTO.getLastName());
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setDateOfBirth(studentDTO.getDateOfBirth());
        student.setGender(studentDTO.getGender());
        student.setAddress(studentDTO.getAddress());
        student.setGradeLevel(studentDTO.getGradeLevel());

        return student;
    }
}
