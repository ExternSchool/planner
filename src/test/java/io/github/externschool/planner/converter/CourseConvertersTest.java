package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.CourseDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.CourseService;
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
public class CourseConvertersTest {
    @Autowired CourseService courseService;
    @Autowired private ConversionService conversionService;
    private Course expectedCourse;
    private CourseDTO expectedDTO;

    @Before
    public void setUp() {
        String firstName = "John";
        String patronymicName = "Johnovich";
        String lastName = "Doe";
        String phoneNumber = "(099)999-9999";
        LocalDate dateOfBirth = LocalDate.of(2010, 7, 9);
        Gender gender = Gender.MALE;
        String address = "Khreschatyk St, 1, Kyiv, Ukraine, 02000";
        GradeLevel gradeLevel = GradeLevel.LEVEL_1;
        Student student = new Student(
                1L,
                firstName,
                patronymicName,
                lastName,
                phoneNumber,
                new VerificationKey(),
                dateOfBirth,
                gender,
                address,
                gradeLevel);
        SchoolSubject subject = new SchoolSubject();
        final StudyPlan plan = new StudyPlan(gradeLevel, subject);
        plan.setId(3L);
        plan.setTitle("Study Plan");
        Teacher teacher = new Teacher();
        teacher.setId(4L);
        teacher.setFirstName("Teacher");
        teacher.setPatronymicName("Teach");
        teacher.setLastName("Teacher");

        expectedCourse = new Course(student.getId(), plan.getId());
        expectedCourse.setTeacher(teacher);
        expectedCourse.setTitle(plan.getTitle() + " " + student.getGradeLevel().toString());
        expectedCourse.setInterviewSemesterOneScore(10);
        expectedCourse.setInterviewSemesterTwoScore(7);
        expectedCourse.setApprovalSemesterOne(true);
        expectedCourse.setApprovalSemesterTwo(false);
        expectedCourse.setExamSemesterOneScore(9);
        expectedCourse.setExamSemesterTwoScore(0);
        expectedCourse.setFinalScore(0);

        expectedDTO = new CourseDTO(student.getId(), plan.getId());
        expectedDTO.setTeacher(teacher);
        expectedDTO.setTitle(plan.getTitle() + " " + student.getGradeLevel().toString());
        expectedDTO.setInterviewSemesterOneScore(10);
        expectedDTO.setInterviewSemesterTwoScore(7);
        expectedDTO.setApprovalSemesterOne(true);
        expectedDTO.setApprovalSemesterTwo(false);
        expectedDTO.setExamSemesterOneScore(9);
        expectedDTO.setExamSemesterTwoScore(0);
        expectedDTO.setFinalScore(0);
    }

    @Test
    public void shouldReturnExpectedDTO_whenOk() {
        CourseDTO actualDTO = conversionService.convert(expectedCourse, CourseDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedDTO);
    }

    @Test
    public void shouldReturnExpectedCourse_whenOk() {
        Course actualCourse = conversionService.convert(expectedDTO, Course.class);

        assertThat(actualCourse)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedCourse);
    }
}
