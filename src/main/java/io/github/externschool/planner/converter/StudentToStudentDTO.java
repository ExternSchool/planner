package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.profile.Student;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentToStudentDTO implements Converter<Student, StudentDTO> {

    @Override
    public StudentDTO convert(final Student student) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setVerificationKey(student.getVerificationKey());
        studentDTO.setFirstName(student.getFirstName());
        studentDTO.setPatronymicName(student.getPatronymicName());
        studentDTO.setLastName(student.getLastName());
        studentDTO.setPhoneNumber(student.getPhoneNumber());
        studentDTO.setDateOfBirth(student.getDateOfBirth());
        studentDTO.setGender(student.getGender());
        studentDTO.setAddress(student.getAddress());
        studentDTO.setGradeLevel(student.getGradeLevel());

        return studentDTO;
    }

}
