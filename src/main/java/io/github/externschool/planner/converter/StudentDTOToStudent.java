package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentDTOToStudent implements Converter<StudentDTO, Student> {
    @Autowired private VerificationKeyService keyService;

    @Override
    public Student convert(final StudentDTO studentDTO) {
        Student student = new Student();
        student.setId(studentDTO.getId());
        student.addVerificationKey(keyService.findKeyByValue(studentDTO.getVerificationKeyValue()));
        student.setFirstName(studentDTO.getFirstName());
        student.setPatronymicName(studentDTO.getPatronymicName());
        student.setLastName(studentDTO.getLastName());
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setDateOfBirth(studentDTO.getDateOfBirth());
        student.setGender(studentDTO.getGender());
        student.setAddress(studentDTO.getAddress());
        student.setGradeLevel(GradeLevel.valueOf(studentDTO.getGradeLevel()));

        return student;
    }
}
