package io.github.externschool.planner.entity.course;

import io.github.externschool.planner.entity.profile.Teacher;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "Course")
@Table(name = "course")
public class Course {

    @EmbeddedId
    private CourseId courseId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(name = "interview_s1_score")
    private Integer interviewSemesterOneScore;
    @Column(name = "interview_s2_score")
    private Integer interviewSemesterTwoScore;
    @Column(name = "exam_s1_score")
    private Integer examSemesterOneScore;
    @Column(name = "exam_s2_score")
    private Integer examSemesterTwoScore;
    @Column(name = "final_score")
    private Integer finalScore;

    private Course() {
    }

    public Course(final CourseId courseId) {
        this.courseId = courseId;
    }

    public CourseId getCourseId() {
        return courseId;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(final Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Course course = (Course) o;
        return Objects.equals(courseId, course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    @Override
    public String toString() {
        return "Course{" +
                "student=" + courseId.getStudentId() +
                ", plan=" + courseId.getPlanId() +
                ", teacher=" + teacher +
                '}';
    }
}
