package io.github.externschool.planner.entity.course;

import io.github.externschool.planner.entity.profile.Teacher;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
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

    @ManyToOne
    @JoinColumn(name = "teacher_id", foreignKey = @ForeignKey(name = "fk_teacher"))
    private Teacher teacher;

    @Column(name = "title")
    private String title;
    @Column(name = "approval_s1")
    private Boolean approvalSemesterOne;
    @Column(name = "approval_s2")
    private Boolean approvalSemesterTwo;
    @Column(name = "interview_s1")
    private Integer interviewScoreSemesterOne;
    @Column(name = "interview_s2")
    private Integer interviewScoreSemesterTwo;
    @Column(name = "exam_s1")
    private Integer examScoreSemesterOne;
    @Column(name = "exam_s2")
    private Integer examScoreSemesterTwo;
    @Column(name = "final_s1")
    private Integer finalScoreSemesterOne;
    @Column(name = "final_s2")
    private Integer finalScoreSemesterTwo;
    @Column(name = "final_score")
    private Integer finalResultScore;

    private Course() {
    }

    public Course(@NotNull final Long studentId, @NotNull final Long planId) {
        this.id = new CoursePK(studentId, planId);
    }

    CoursePK getId() {
        return id;
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

    public Integer getInterviewScoreSemesterOne() {
        return interviewScoreSemesterOne;
    }

    public void setInterviewScoreSemesterOne(final Integer interviewScoreSemesterOne) {
        this.interviewScoreSemesterOne = interviewScoreSemesterOne;
    }

    public Integer getInterviewScoreSemesterTwo() {
        return interviewScoreSemesterTwo;
    }

    public void setInterviewScoreSemesterTwo(final Integer interviewScoreSemesterTwo) {
        this.interviewScoreSemesterTwo = interviewScoreSemesterTwo;
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

    public Integer getExamScoreSemesterOne() {
        return examScoreSemesterOne;
    }

    public void setExamScoreSemesterOne(final Integer examScoreSemesterOne) {
        this.examScoreSemesterOne = examScoreSemesterOne;
    }

    public Integer getExamScoreSemesterTwo() {
        return examScoreSemesterTwo;
    }

    public void setExamScoreSemesterTwo(final Integer examScoreSemesterTwo) {
        this.examScoreSemesterTwo = examScoreSemesterTwo;
    }

    public Integer getFinalScoreSemesterOne() {
        return finalScoreSemesterOne;
    }

    public void setFinalScoreSemesterOne(final Integer finalScoreSemesterOne) {
        this.finalScoreSemesterOne = finalScoreSemesterOne;
    }

    public Integer getFinalScoreSemesterTwo() {
        return finalScoreSemesterTwo;
    }

    public void setFinalScoreSemesterTwo(final Integer finalScoreSemesterTwo) {
        this.finalScoreSemesterTwo = finalScoreSemesterTwo;
    }

    public Integer getFinalResultScore() {
        return finalResultScore;
    }

    public void setFinalResultScore(final Integer finalResultScore) {
        this.finalResultScore = finalResultScore;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;

        final Course course = (Course) o;

        if (!id.equals(course.id)) return false;
        if (getTeacher() != null ? !getTeacher().equals(course.getTeacher()) : course.getTeacher() != null)
            return false;
        if (getTitle() != null ? !getTitle().equals(course.getTitle()) : course.getTitle() != null) return false;
        if (getApprovalSemesterOne() != null
                ? !getApprovalSemesterOne().equals(course.getApprovalSemesterOne())
                : course.getApprovalSemesterOne() != null) {
            return false;
        }
        if (getApprovalSemesterTwo() != null
                ? !getApprovalSemesterTwo().equals(course.getApprovalSemesterTwo())
                : course.getApprovalSemesterTwo() != null) {
            return false;
        }
        if (getInterviewScoreSemesterOne() != null
                ? !getInterviewScoreSemesterOne().equals(course.getInterviewScoreSemesterOne())
                : course.getInterviewScoreSemesterOne() != null)
            return false;
        if (getInterviewScoreSemesterTwo() != null
                ? !getInterviewScoreSemesterTwo().equals(course.getInterviewScoreSemesterTwo())
                : course.getInterviewScoreSemesterTwo() != null)
            return false;
        if (getExamScoreSemesterOne() != null
                ? !getExamScoreSemesterOne().equals(course.getExamScoreSemesterOne())
                : course.getExamScoreSemesterOne() != null)
            return false;
        if (getExamScoreSemesterTwo() != null
                ? !getExamScoreSemesterTwo().equals(course.getExamScoreSemesterTwo())
                : course.getExamScoreSemesterTwo() != null)
            return false;
        if (getFinalScoreSemesterOne() != null
                ? !getFinalScoreSemesterOne().equals(course.getFinalScoreSemesterOne())
                : course.getFinalScoreSemesterOne() != null)
            return false;
        if (getFinalScoreSemesterTwo() != null
                ? !getFinalScoreSemesterTwo().equals(course.getFinalScoreSemesterTwo())
                : course.getFinalScoreSemesterTwo() != null)
            return false;
        return getFinalResultScore() != null
                ? getFinalResultScore().equals(course.getFinalResultScore())
                : course.getFinalResultScore() == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getApprovalSemesterOne() != null ? getApprovalSemesterOne().hashCode() : 0);
        result = 31 * result + (getApprovalSemesterTwo() != null ? getApprovalSemesterTwo().hashCode() : 0);
        result = 31 * result + (getInterviewScoreSemesterOne() != null ? getInterviewScoreSemesterOne().hashCode() : 0);
        result = 31 * result + (getInterviewScoreSemesterTwo() != null ? getInterviewScoreSemesterTwo().hashCode() : 0);
        result = 31 * result + (getExamScoreSemesterOne() != null ? getExamScoreSemesterOne().hashCode() : 0);
        result = 31 * result + (getExamScoreSemesterTwo() != null ? getExamScoreSemesterTwo().hashCode() : 0);
        result = 31 * result + (getFinalScoreSemesterOne() != null ? getFinalScoreSemesterOne().hashCode() : 0);
        result = 31 * result + (getFinalScoreSemesterTwo() != null ? getFinalScoreSemesterTwo().hashCode() : 0);
        result = 31 * result + (getFinalResultScore() != null ? getFinalResultScore().hashCode() : 0);
        return result;
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

        CoursePK() {
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
