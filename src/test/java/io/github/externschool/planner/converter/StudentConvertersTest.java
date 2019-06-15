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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class StudentConvertersTest {
    @Autowired private ConversionService conversionService;
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
        GradeLevel gradeLevel = GradeLevel.LEVEL_1;

        expectedStudent = new Student();
        expectedStudent.addVerificationKey(verificationKey);
        expectedStudent.setFirstName(firstName);
        expectedStudent.setPatronymicName(patronymicName);
        expectedStudent.setLastName(lastName);
        expectedStudent.setPhoneNumber(phoneNumber);
        expectedStudent.setGradeLevel(gradeLevel);

        expectedDTO = new StudentDTO();
        expectedDTO.setVerificationKey(verificationKey);
        expectedDTO.setEmail(verificationKey.getUser().getEmail());
        expectedDTO.setFirstName(firstName);
        expectedDTO.setPatronymicName(patronymicName);
        expectedDTO.setLastName(lastName);
        expectedDTO.setPhoneNumber(phoneNumber);
        expectedDTO.setGradeLevel(gradeLevel.ordinal());
    }

    @Test
    public void shouldReturnExpectedDTO_whenOk() {
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
    public void shouldReturnEmailStudentDTO_whenStudentsKeyHasUser() {
        StudentDTO actualDTO = conversionService.convert(expectedStudent, StudentDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedDTO, "email")
                .hasFieldOrPropertyWithValue("email", expectedDTO.getEmail());
    }

    @Test
    public void shouldReturnExpectedStudent_whenOk() {
        Student actualStudent = conversionService.convert(expectedDTO, Student.class);

        assertThat(actualStudent)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedStudent);
    }

    @Test
    public void shouldReturnZeroGradeLevelStudentDTO_whenStudentsGradeLevelNull() {
        expectedStudent.setGradeLevel(null);

        StudentDTO actualDTO = conversionService.convert(expectedStudent, StudentDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedDTO, "gradeLevel")
                .hasFieldOrPropertyWithValue("gradeLevel", 0);
    }

    @Test
    public void shouldReturnLevelNotDefinedStudent_whenStudentDTOHasInvalidGradeLevel() {
        expectedDTO.setGradeLevel(13);

        Student actualStudent = conversionService.convert(expectedDTO, Student.class);

        assertThat(actualStudent)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedStudent, "gradeLevel")
                .hasFieldOrPropertyWithValue("gradeLevel", GradeLevel.LEVEL_NOT_DEFINED);
    }
}
