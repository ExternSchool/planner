package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.CourseDTO;
import io.github.externschool.planner.entity.course.Course;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CourseDTOToCourse implements Converter<CourseDTO, Course> {
    @Override
    public Course convert(CourseDTO courseDTO) {
        Course course = new Course(courseDTO.getStudentId(), courseDTO.getPlanId());
        BeanUtils.copyProperties(courseDTO, course);

        return course;
    }
}
