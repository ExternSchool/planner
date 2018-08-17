package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.profile.Teacher;

import java.util.Objects;

public class CourseDTO {
    private Long studentId;
    private Long planId;
    private Teacher teacher;
    private String title;

    private Integer interviewSemesterOneScore;
    private Integer interviewSemesterTwoScore;
    private Boolean approvalSemesterOne;
    private Boolean approvalSemesterTwo;
    private Integer examSemesterOneScore;
    private Integer examSemesterTwoScore;
    private Integer semesterOneScore;
    private Integer semesterTwoScore;
    private Integer finalScore;

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

    public Integer getSemesterOneScore() {
        return semesterOneScore;
    }

    public void setSemesterOneScore(final Integer semesterOneScore) {
        this.semesterOneScore = semesterOneScore;
    }

    public Integer getSemesterTwoScore() {
        return semesterTwoScore;
    }

    public void setSemesterTwoScore(final Integer semesterTwoScore) {
        this.semesterTwoScore = semesterTwoScore;
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
        final CourseDTO courseDTO = (CourseDTO) o;
        return Objects.equals(studentId, courseDTO.studentId) &&
                Objects.equals(planId, courseDTO.planId) &&
                Objects.equals(teacher, courseDTO.teacher) &&
                Objects.equals(title, courseDTO.title) &&
                Objects.equals(interviewSemesterOneScore, courseDTO.interviewSemesterOneScore) &&
                Objects.equals(interviewSemesterTwoScore, courseDTO.interviewSemesterTwoScore) &&
                Objects.equals(approvalSemesterOne, courseDTO.approvalSemesterOne) &&
                Objects.equals(approvalSemesterTwo, courseDTO.approvalSemesterTwo) &&
                Objects.equals(examSemesterOneScore, courseDTO.examSemesterOneScore) &&
                Objects.equals(examSemesterTwoScore, courseDTO.examSemesterTwoScore) &&
                Objects.equals(semesterOneScore, courseDTO.semesterOneScore) &&
                Objects.equals(semesterTwoScore, courseDTO.semesterTwoScore) &&
                Objects.equals(finalScore, courseDTO.finalScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, planId, teacher, title, interviewSemesterOneScore, interviewSemesterTwoScore,
                approvalSemesterOne, approvalSemesterTwo, examSemesterOneScore, examSemesterTwoScore,
                semesterOneScore, semesterTwoScore, finalScore);
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
