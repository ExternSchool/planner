package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.profile.Teacher;

import java.util.Objects;

public class CourseDTO {
    private Long studentId;
    private Long planId;
    private Teacher teacher;
    private String title;
    private Boolean approvalSemesterOne;
    private Boolean approvalSemesterTwo;
    private Integer interviewScoreSemesterOne;
    private Integer interviewScoreSemesterTwo;
    private Integer examScoreSemesterOne;
    private Integer examScoreSemesterTwo;
    private Integer finalScoreSemesterOne;
    private Integer finalScoreSemesterTwo;
    private Integer finalScoreForYear;

    public CourseDTO(final Long studentId, final Long planId) {
        this.studentId = studentId;
        this.planId = planId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getPlanId() {
        return planId;
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

    public Integer getFinalScoreForYear() {
        return finalScoreForYear;
    }

    public void setFinalScoreForYear(final Integer finalScoreForYear) {
        this.finalScoreForYear = finalScoreForYear;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseDTO)) return false;
        final CourseDTO courseDTO = (CourseDTO) o;
        return Objects.equals(studentId, courseDTO.studentId) &&
                Objects.equals(planId, courseDTO.planId) &&
                Objects.equals(teacher, courseDTO.teacher) &&
                Objects.equals(title, courseDTO.title) &&
                Objects.equals(interviewScoreSemesterOne, courseDTO.interviewScoreSemesterOne) &&
                Objects.equals(interviewScoreSemesterTwo, courseDTO.interviewScoreSemesterTwo) &&
                Objects.equals(approvalSemesterOne, courseDTO.approvalSemesterOne) &&
                Objects.equals(approvalSemesterTwo, courseDTO.approvalSemesterTwo) &&
                Objects.equals(examScoreSemesterOne, courseDTO.examScoreSemesterOne) &&
                Objects.equals(examScoreSemesterTwo, courseDTO.examScoreSemesterTwo) &&
                Objects.equals(finalScoreSemesterOne, courseDTO.finalScoreSemesterOne) &&
                Objects.equals(finalScoreSemesterTwo, courseDTO.finalScoreSemesterTwo) &&
                Objects.equals(finalScoreForYear, courseDTO.finalScoreForYear);
    }

    @Override
    public int hashCode() {

        return Objects.hash(studentId, planId, teacher, title, interviewScoreSemesterOne, interviewScoreSemesterTwo, approvalSemesterOne, approvalSemesterTwo, examScoreSemesterOne, examScoreSemesterTwo, finalScoreSemesterOne, finalScoreSemesterTwo, finalScoreForYear);
    }

    @Override
    public String toString() {
        return "CourseDTO{" +
                "studentId=" + studentId +
                ", planId=" + planId +
                ", teacherId=" + (teacher != null ? teacher.getId() : "") +
                '}';
    }
}
