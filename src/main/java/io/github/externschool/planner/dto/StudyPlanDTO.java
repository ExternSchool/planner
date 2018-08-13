package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;

import java.util.Objects;

public class StudyPlanDTO {
    private Long id;
    private GradeLevel gradeLevel;
    private SchoolSubject subject;
    private String title;
    private Integer hoursPerSemesterOne;
    private Integer hoursPerSemesterTwo;
    private Boolean examSemesterOne;
    private Boolean examSemesterTwo;

    public StudyPlanDTO(){
    }

    public StudyPlanDTO(Long id,
                        GradeLevel gradeLevel,
                        SchoolSubject subject,
                        String title,
                        Integer hoursPerSemesterOne,
                        Integer hoursPerSemesterTwo,
                        Boolean examSemesterOne,
                        Boolean examSemesterTwo) {
        this.id = id;
        this.gradeLevel = gradeLevel;
        this.subject = subject;
        this.title = title;
        this.hoursPerSemesterOne = hoursPerSemesterOne;
        this.hoursPerSemesterTwo = hoursPerSemesterTwo;
        this.examSemesterOne = examSemesterOne;
        this.examSemesterTwo = examSemesterTwo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public SchoolSubject getSubject() {
        return subject;
    }

    public void setSubject(SchoolSubject subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHoursPerSemesterOne() {
        return hoursPerSemesterOne;
    }

    public void setHoursPerSemesterOne(Integer hoursPerSemesterOne) {
        this.hoursPerSemesterOne = hoursPerSemesterOne;
    }

    public Integer getHoursPerSemesterTwo() {
        return hoursPerSemesterTwo;
    }

    public void setHoursPerSemesterTwo(Integer hoursPerSemesterTwo) {
        this.hoursPerSemesterTwo = hoursPerSemesterTwo;
    }

    public Boolean getExamSemesterOne() {
        return examSemesterOne;
    }

    public void setExamSemesterOne(Boolean examSemesterOne) {
        this.examSemesterOne = examSemesterOne;
    }

    public Boolean getExamSemesterTwo() {
        return examSemesterTwo;
    }

    public void setExamSemesterTwo(Boolean examSemesterTwo) {
        this.examSemesterTwo = examSemesterTwo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudyPlanDTO)) return false;
        StudyPlanDTO that = (StudyPlanDTO) o;
        return Objects.equals(id, that.id) &&
                gradeLevel == that.gradeLevel &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(title, that.title) &&
                Objects.equals(hoursPerSemesterOne, that.hoursPerSemesterOne) &&
                Objects.equals(hoursPerSemesterTwo, that.hoursPerSemesterTwo) &&
                Objects.equals(examSemesterOne, that.examSemesterOne) &&
                Objects.equals(examSemesterTwo, that.examSemesterTwo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gradeLevel, subject, title, hoursPerSemesterOne,
                hoursPerSemesterTwo, examSemesterOne, examSemesterTwo);
    }
}
