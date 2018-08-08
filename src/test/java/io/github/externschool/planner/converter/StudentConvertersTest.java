package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;
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
        VerificationKey verificationKey = new VerificationKey();
        User user = new User("some@email.com", "pass");
        user.addVerificationKey(verificationKey);
        String firstName = "John";
        String patronymicName = "Johnovich";
        String lastName = "Doe";
        String phoneNumber = "(099)999-9999";
        LocalDate dateOfBirth = LocalDate.of(2010, 7, 9);
        Gender gender = Gender.MALE;
        String address = "Khreschatyk St, 1, Kyiv, Ukraine, 02000";
        GradeLevel gradeLevel = GradeLevel.LEVEL_1;

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
        expectedDTO.setEmail(verificationKey.getUser().getEmail());
        expectedDTO.setFirstName(firstName);
        expectedDTO.setPatronymicName(patronymicName);
        expectedDTO.setLastName(lastName);
        expectedDTO.setPhoneNumber(phoneNumber);
        expectedDTO.setDateOfBirth(dateOfBirth);
        expectedDTO.setGender(gender);
        expectedDTO.setAddress(address);
        expectedDTO.setGradeLevel(gradeLevel);
    }

    @Test
    public void shouldReturnExpectedDTO() {
        StudentDTO actualDTO = conversionService.convert(expectedStudent, StudentDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedDTO);
    }

    @Test
    public void shouldReturnEmptyEmailStudentDTO_whenStudentsKeyHasNoUser() {
        expectedStudent.getVerificationKey().getUser().removeVerificationKey();

        StudentDTO actualDTO = conversionService.convert(expectedStudent, StudentDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedDTO, "email")
                .hasFieldOrPropertyWithValue("email", "");
    }

    @Test
    public void shouldReturnExpectedStudent() {
        Student actualStudent = conversionService.convert(expectedDTO, Student.class);

        assertThat(actualStudent)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedStudent);
    }
}
