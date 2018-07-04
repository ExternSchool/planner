package io.github.externschool.planner.converter;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlannerApplication.class)
public class TeacherConvertersTest {
    @Autowired
    ConversionService conversionService;
    private Teacher expectedTeacher;
    private TeacherDTO expectedDTO;

    @Before
    public void setUp() {
        final Long id = 1L;
        final String verificationKey = "0123456789qwertyuiopAJHGDKJADGKJD";
        final String firstName = "John";
        final String patronymicName = "Johnovich";
        final String lastName = "Doe";
        final String phoneNumber = "(099)999-9999";
        final String officer = "Principal";
        SchoolSubject firstSubject = new SchoolSubject();
        firstSubject.setName("Quantum Mechanics");
        SchoolSubject secondSubject = new SchoolSubject();
        secondSubject.setName("Algebraic topology");
        final Set<SchoolSubject> schoolSubjects = new HashSet<>(Arrays.asList(firstSubject, secondSubject));

        expectedTeacher = new Teacher();
        expectedTeacher.setId(id);
        expectedTeacher.setFirstName(firstName);
        expectedTeacher.setPatronymicName(patronymicName);
        expectedTeacher.setLastName(lastName);
        expectedTeacher.setPhoneNumber(phoneNumber);
        expectedTeacher.setVerificationKey(verificationKey);
        expectedTeacher.setOfficer(officer);
        expectedTeacher.setSubjects(schoolSubjects);

        expectedDTO = new TeacherDTO();
        expectedDTO.setId(id);
        expectedDTO.setFirstName(firstName);
        expectedDTO.setPatronymicName(patronymicName);
        expectedDTO.setLastName(lastName);
        expectedDTO.setPhoneNumber(phoneNumber);
        expectedDTO.setVerificationKey(verificationKey);
        expectedDTO.setOfficer(officer);
        expectedDTO.setSchoolSubjects(schoolSubjects);
    }

    @Test
    public void shouldReturnExpectedDTO() {
        TeacherDTO actualDTO = conversionService.convert(expectedTeacher, TeacherDTO.class);
        assertThat(actualDTO.getFirstName())
                .isEqualTo(expectedDTO.getFirstName());
    }

    @Test
    public void shouldReturnExpectedTeacher() {
        Teacher actualTeacher = conversionService.convert(expectedDTO, Teacher.class);
        assertThat(actualTeacher)
                .isNotNull()
                .isEqualTo(expectedTeacher)
                .isEqualToComparingFieldByField(expectedTeacher);
    }
}


