package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.repository.CourseRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CourseFormatterTest {
    @Mock private CourseRepository courseRepository;
    private CourseFormatter courseFormatter;

    private Course course = new Course(1L, 2L);

    @Before
    public void setup(){
        courseFormatter = new CourseFormatter(courseRepository);
        course.setTitle("Math");

        Mockito.when(courseRepository.findById_StudentIdAndId_PlanId(1L, 2L))
                .thenReturn(course);
    }

    @Test
    public void shouldReturnSameCourse_whenParsePrint() throws ParseException {
        Locale locale = new Locale("uk");
        String printed = courseFormatter.print(course, locale);
        Course actualCourse = courseFormatter.parse(printed, locale);

        assertThat(actualCourse)
                .isEqualTo(course)
                .isEqualToComparingFieldByField(course);
    }
}
