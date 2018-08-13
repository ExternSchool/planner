package io.github.externschool.planner.entity.course;

import io.github.externschool.planner.entity.profile.Teacher;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "course")
public class Course implements Serializable {
    @EmbeddedId private CoursePK id;

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(name = "title")
    private String title;
    @Column(name = "interview_s1_score")
    private Integer interviewSemesterOneScore;
    @Column(name = "interview_s2_score")
    private Integer interviewSemesterTwoScore;
    @Column(name = "approval_s1")
    private Boolean approvalSemesterOne;
    @Column(name = "approval_s2")
    private Boolean approvalSemesterTwo;
    @Column(name = "exam_s1_score")
    private Integer examSemesterOneScore;
    @Column(name = "exam_s2_score")
    private Integer examSemesterTwoScore;
    @Column(name = "final_score")
    private Integer finalScore;

    private Course() {
    }

    public Course(@NotNull final Long studentId, @NotNull final Long planId) {
        this.id = new CoursePK(studentId, planId);
    }

    public Long getStudentId() {
        return id.getStudentId();
    }

    public Long getPlanId() {
        return id.getPlanId();
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(final Teacher teacher) {
        this.teacher = teacher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Integer getInterviewSemesterOneScore() {
        return interviewSemesterOneScore;
    }

    public void setInterviewSemesterOneScore(final Integer interviewSemesterOneScore) {
        this.interviewSemesterOneScore = interviewSemesterOneScore;
    }

    public Integer getInterviewSemesterTwoScore() {
        return interviewSemesterTwoScore;
    }

    public void setInterviewSemesterTwoScore(final Integer interviewSemesterTwoScore) {
        this.interviewSemesterTwoScore = interviewSemesterTwoScore;
    }

    public Boolean getApprovalSemesterOne() {
        return approvalSemesterOne;
    }

    public void setApprovalSemesterOne(final Boolean approvalSemesterOne) {
        this.approvalSemesterOne = approvalSemesterOne;
    }

    public Boolean getApprovalSemesterTwo() {
        return approvalSemesterTwo;
    }

    public void setApprovalSemesterTwo(final Boolean approvalSemesterTwo) {
        this.approvalSemesterTwo = approvalSemesterTwo;
    }

    public Integer getExamSemesterOneScore() {
        return examSemesterOneScore;
    }

    public void setExamSemesterOneScore(final Integer examSemesterOneScore) {
        this.examSemesterOneScore = examSemesterOneScore;
    }

    public Integer getExamSemesterTwoScore() {
        return examSemesterTwoScore;
    }

    public void setExamSemesterTwoScore(final Integer examSemesterTwoScore) {
        this.examSemesterTwoScore = examSemesterTwoScore;
    }

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(final Integer finalScore) {
        this.finalScore = finalScore;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Course course = (Course) o;
        return Objects.equals(id, course.id) &&
                Objects.equals(teacher, course.teacher) &&
                Objects.equals(interviewSemesterOneScore, course.interviewSemesterOneScore) &&
                Objects.equals(interviewSemesterTwoScore, course.interviewSemesterTwoScore) &&
                Objects.equals(approvalSemesterOne, course.approvalSemesterOne) &&
                Objects.equals(approvalSemesterTwo, course.approvalSemesterTwo) &&
                Objects.equals(examSemesterOneScore, course.examSemesterOneScore) &&
                Objects.equals(examSemesterTwoScore, course.examSemesterTwoScore) &&
                Objects.equals(finalScore, course.finalScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, interviewSemesterOneScore, interviewSemesterTwoScore, approvalSemesterOne,
                approvalSemesterTwo, examSemesterOneScore, examSemesterTwoScore, finalScore);
    }

    @Override
    public String toString() {
        return "Course{" +
                "studentId=" + getStudentId() +
                ", planId=" + getPlanId() +
                ", teacherId=" + (teacher != null ? teacher.getId() : "") +
                '}';
    }

    @Embeddable
    public static class CoursePK implements Serializable {
        @NotNull
        @Column(name = "student_id")
        private Long studentId;

        @NotNull
        @Column(name = "plan_id")
        private Long planId;

        private CoursePK() {
        }

        CoursePK(@NotNull final Long studentId, @NotNull final Long planId) {
            this.studentId = studentId;
            this.planId = planId;
        }

        Long getStudentId() {
            return studentId;
        }

        Long getPlanId() {
            return planId;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final CoursePK coursePK = (CoursePK) o;
            return Objects.equals(studentId, coursePK.studentId) &&
                    Objects.equals(planId, coursePK.planId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(studentId, planId);
        }

        @Override
        public String toString() {
            return "CoursePK{" +
                    "studentId=" + studentId +
                    ", planId=" + planId +
                    '}';
        }
    }
}
