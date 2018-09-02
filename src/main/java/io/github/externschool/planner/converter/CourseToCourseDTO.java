package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.CourseDTO;
import io.github.externschool.planner.entity.course.Course;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CourseToCourseDTO implements Converter<Course, CourseDTO> {
    @Override
    public CourseDTO convert(Course course) {
        CourseDTO courseDTO = new CourseDTO(course.getStudentId(), course.getPlanId());
        BeanUtils.copyProperties(course, courseDTO);

        return courseDTO;
    }
}
