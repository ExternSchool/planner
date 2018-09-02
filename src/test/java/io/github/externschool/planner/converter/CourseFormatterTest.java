package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.repository.CourseRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseFormatterTest {
    @Mock private CourseRepository repository;
    private CourseFormatter formatter;

    private Course course;

    @Before
    public void setup() {
        formatter = new CourseFormatter(repository);

        StudyPlan plan = new StudyPlan();
        plan.setId(1L);
        Student student = new Student();
        student.setId(2L);
        course = new Course(student.getId(), plan.getId());

        Mockito.when(repository.findById_StudentIdAndId_PlanId(student.getId(), plan.getId()))
                .thenReturn(course);
    }

    @Test
    public void shouldReturnSameCourse_whenRunParsePrint() throws ParseException {
        Locale locale = new Locale("uk");

        String printed = formatter.print(course, locale);
        Course actualCourse = formatter.parse(printed, locale);

        assertThat(actualCourse)
                .isNotNull()
                .isEqualTo(course)
                .isEqualToComparingFieldByField(course);
    }
}
