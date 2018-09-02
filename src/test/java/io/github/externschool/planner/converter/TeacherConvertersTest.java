package io.github.externschool.planner.converter;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlannerApplication.class)
public class TeacherConvertersTest {
    @Autowired ConversionService conversionService;

    private Teacher expectedTeacher;
    private TeacherDTO expectedDTO;

    @Before
    public void setUp() {
        VerificationKey verificationKey = new VerificationKey();
        User user = new User("some@email.com", "pass");
        user.addVerificationKey(verificationKey);
        Long id = 1L;
        String firstName = "John";
        String patronymicName = "Johnovich";
        String lastName = "Doe";
        String phoneNumber = "(099)999-9999";
        String officer = "Principal";
        Set<SchoolSubject> schoolSubjects = Stream.of("Quantum Mechanics", "Algebraic Topology")
                .map(s -> {
                    SchoolSubject subject = new SchoolSubject();
                    subject.setTitle(s);
                    return subject;
                })
                .collect(Collectors.toSet());

        expectedTeacher = new Teacher();
        expectedTeacher.setId(id);
        expectedTeacher.setFirstName(firstName);
        expectedTeacher.setPatronymicName(patronymicName);
        expectedTeacher.setLastName(lastName);
        expectedTeacher.setPhoneNumber(phoneNumber);
        expectedTeacher.addVerificationKey(verificationKey);
        expectedTeacher.setOfficer(officer);
        schoolSubjects.forEach(expectedTeacher::addSubject);

        expectedDTO = new TeacherDTO();
        expectedDTO.setId(id);
        expectedDTO.setVerificationKey(verificationKey);
        expectedDTO.setEmail(verificationKey.getUser().getEmail());
        expectedDTO.setFirstName(firstName);
        expectedDTO.setPatronymicName(patronymicName);
        expectedDTO.setLastName(lastName);
        expectedDTO.setPhoneNumber(phoneNumber);
        expectedDTO.setOfficer(officer);
        expectedDTO.setSchoolSubjects(schoolSubjects);
    }

    @Test
    public void shouldReturnExpectedDTO() {
        TeacherDTO actualDTO = conversionService.convert(expectedTeacher, TeacherDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualTo(expectedDTO)
                .isEqualToComparingFieldByField(expectedDTO);
    }

    @Test
    public void shouldReturnEmptyEmailPersonDTO_whenPersonsKeyHasNoUser() {
        expectedTeacher.getVerificationKey().getUser().removeVerificationKey();

        TeacherDTO actualDTO = conversionService.convert(expectedTeacher, TeacherDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedDTO, "email")
                .hasFieldOrPropertyWithValue("email", "");
    }

    @Test
    public void shouldReturnExpectedTeacher() {
        Teacher actualTeacher = conversionService.convert(expectedDTO, Teacher.class);

        assertThat(actualTeacher)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedTeacher, "verificationKey");
        assertThat(actualTeacher.getVerificationKey())
                .isEqualToIgnoringGivenFields(expectedTeacher.getVerificationKey(), "person");
    }
}
