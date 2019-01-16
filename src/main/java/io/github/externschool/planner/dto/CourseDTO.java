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
    private Integer finalResultScore;

    private Integer hoursPerSemesterOne;
    private Integer hoursPerSemesterTwo;
    private Integer worksPerSemesterOne;
    private Integer worksPerSemesterTwo;

    private String optionalData;

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

    public Integer getFinalResultScore() {
        return finalResultScore;
    }

    public void setFinalResultScore(final Integer finalResultScore) {
        this.finalResultScore = finalResultScore;
    }

    public Integer getHoursPerSemesterOne() {
        return hoursPerSemesterOne;
    }

    public void setHoursPerSemesterOne(final Integer hoursPerSemesterOne) {
        this.hoursPerSemesterOne = hoursPerSemesterOne;
    }

    public Integer getHoursPerSemesterTwo() {
        return hoursPerSemesterTwo;
    }

    public void setHoursPerSemesterTwo(final Integer hoursPerSemesterTwo) {
        this.hoursPerSemesterTwo = hoursPerSemesterTwo;
    }

    public Integer getWorksPerSemesterOne() {
        return worksPerSemesterOne;
    }

    public void setWorksPerSemesterOne(final Integer worksPerSemesterOne) {
        this.worksPerSemesterOne = worksPerSemesterOne;
    }

    public Integer getWorksPerSemesterTwo() {
        return worksPerSemesterTwo;
    }

    public void setWorksPerSemesterTwo(final Integer worksPerSemesterTwo) {
        this.worksPerSemesterTwo = worksPerSemesterTwo;
    }

    public String getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(final String optionalData) {
        this.optionalData = optionalData;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CourseDTO courseDTO = (CourseDTO) o;
        return Objects.equals(getStudentId(), courseDTO.getStudentId()) &&
                Objects.equals(getPlanId(), courseDTO.getPlanId()) &&
                Objects.equals(getTeacher(), courseDTO.getTeacher()) &&
                Objects.equals(getTitle(), courseDTO.getTitle()) &&
                Objects.equals(getApprovalSemesterOne(), courseDTO.getApprovalSemesterOne()) &&
                Objects.equals(getApprovalSemesterTwo(), courseDTO.getApprovalSemesterTwo()) &&
                Objects.equals(getInterviewScoreSemesterOne(), courseDTO.getInterviewScoreSemesterOne()) &&
                Objects.equals(getInterviewScoreSemesterTwo(), courseDTO.getInterviewScoreSemesterTwo()) &&
                Objects.equals(getExamScoreSemesterOne(), courseDTO.getExamScoreSemesterOne()) &&
                Objects.equals(getExamScoreSemesterTwo(), courseDTO.getExamScoreSemesterTwo()) &&
                Objects.equals(getFinalScoreSemesterOne(), courseDTO.getFinalScoreSemesterOne()) &&
                Objects.equals(getFinalScoreSemesterTwo(), courseDTO.getFinalScoreSemesterTwo()) &&
                Objects.equals(getFinalResultScore(), courseDTO.getFinalResultScore()) &&
                Objects.equals(getHoursPerSemesterOne(), courseDTO.getHoursPerSemesterOne()) &&
                Objects.equals(getHoursPerSemesterTwo(), courseDTO.getHoursPerSemesterTwo()) &&
                Objects.equals(getWorksPerSemesterOne(), courseDTO.getWorksPerSemesterOne()) &&
                Objects.equals(getWorksPerSemesterTwo(), courseDTO.getWorksPerSemesterTwo()) &&
                Objects.equals(getOptionalData(), courseDTO.getOptionalData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStudentId(), getPlanId(), getTeacher(), getTitle(),
                getApprovalSemesterOne(), getApprovalSemesterTwo(),
                getInterviewScoreSemesterOne(), getInterviewScoreSemesterTwo(),
                getExamScoreSemesterOne(), getExamScoreSemesterTwo(),
                getFinalScoreSemesterOne(), getFinalScoreSemesterTwo(),
                getFinalResultScore(), getHoursPerSemesterOne(), getHoursPerSemesterTwo(),
                getWorksPerSemesterOne(), getWorksPerSemesterTwo(), getOptionalData());
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
