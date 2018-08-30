package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.repository.CourseRepository;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Locale;

@Service
public class CourseFormatter implements Formatter<Course> {
    private final CourseRepository courseRepository;

    public CourseFormatter(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Course parse(final String s, Locale locale) throws ParseException {
        String[] sString = s.split("&");
        String s1 = sString[0];
        String s2 = sString[1];

        return courseRepository.findById_StudentIdAndId_PlanId(Long.parseLong(s1)
                , Long.parseLong(s2));
    }

    @Override
    public String print(final Course course,final Locale locale) {
        return (course !=null && course.getPlanId() != null && course.getStudentId() != null
                ? course.getStudentId().toString() +"&" +course.getPlanId().toString() :"");
    }
}
