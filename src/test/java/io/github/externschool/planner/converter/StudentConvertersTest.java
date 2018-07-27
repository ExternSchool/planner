package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Student;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentConvertersTest {
    @Autowired
    ConversionService conversionService;
    private Student expectedStudent;
    private StudentDTO expectedDTO;

    @Before
    public void setUp() {
        final VerificationKey verificationKey = new VerificationKey();
        final String firstName = "John";
        final String patronymicName = "Johnovich";
        final String lastName = "Doe";
        final String phoneNumber = "(099)999-9999";
        final LocalDate dateOfBirth = LocalDate.of(2018, 7, 9);
        final Gender gender = Gender.MALE;
        final String address = "Khreschatyk St, 1, Kyiv, Ukraine, 02000";
        final GradeLevel gradeLevel = GradeLevel.LEVEL_1;

        expectedStudent = new Student();
        expectedStudent.addVerificationKey(verificationKey);
        expectedStudent.setFirstName(firstName);
        expectedStudent.setPatronymicName(patronymicName);
        expectedStudent.setLastName(lastName);
        expectedStudent.setPhoneNumber(phoneNumber);
        expectedStudent.setDateOfBirth(dateOfBirth);
        expectedStudent.setGender(gender);
        expectedStudent.setAddress(address);
        expectedStudent.setGradeLevel(gradeLevel);

        expectedDTO = new StudentDTO();
        expectedDTO.setVerificationKey(verificationKey);
        expectedDTO.setFirstName(firstName);
        expectedDTO.setPatronymicName(patronymicName);
        expectedDTO.setLastName(lastName);
        expectedDTO.setPhoneNumber(phoneNumber);
        expectedDTO.setDateOfBirth(dateOfBirth);
        expectedDTO.setGender(gender);
        expectedDTO.setAddress(address);
        expectedDTO.setGradeLevel(gradeLevel.ordinal());

    }

    @Test
    public void shouldReturnExpectedDTO() {
        StudentDTO actualDTO = conversionService.convert(expectedStudent, StudentDTO.class);
        assertThat(actualDTO)
                .isNotNull()
                .isEqualTo(expectedDTO)
                .isEqualToComparingFieldByField(expectedDTO);
    }

    @Test
    public void shouldReturnExpectedStudent() {
        Student actualStudent = conversionService.convert(expectedDTO, Student.class);
        assertThat(actualStudent)
                .isNotNull()
                .isEqualTo(expectedStudent)
                .isEqualToComparingFieldByField(expectedStudent);
    }


}
