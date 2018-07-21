package io.github.externschool.planner.entity.course;

import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.profile.Student;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CourseId implements Serializable {

    @NotNull
    private Long studentId;

    @NotNull
    private Long planId;

    private CourseId() {
    }

    public CourseId(final Student student, final StudyPlan plan) {
        studentId = student.getId();
        planId = plan.getId();
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getPlanId() {
        return planId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CourseId courseId = (CourseId) o;
        return Objects.equals(studentId, courseId.studentId) &&
                Objects.equals(planId, courseId.planId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, planId);
    }
}
