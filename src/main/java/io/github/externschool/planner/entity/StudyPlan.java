package io.github.externschool.planner.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "Plan")
@Table(name = "plan")
public class StudyPlan {
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "grade_level")
    @Enumerated
    private GradeLevel gradeLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject", foreignKey = @ForeignKey(name = "SUBJECT_FK"))
    private SchoolSubject subject;

    private Integer hoursPerSemesterOne;
    private Integer hoursPerSemesterTwo;
    private Boolean testPerSemesterOne;
    private Boolean testPerSemesterTwo;

    public StudyPlan() {
    }

    public StudyPlan(final GradeLevel gradeLevel, final SchoolSubject subject) {
        this.gradeLevel = gradeLevel;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(final GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public SchoolSubject getSubject() {
        return subject;
    }

    public void setSubject(final SchoolSubject subject) {
        this.subject = subject;
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

    public Boolean getTestPerSemesterOne() {
        return testPerSemesterOne;
    }

    public void setTestPerSemesterOne(final Boolean testPerSemesterOne) {
        this.testPerSemesterOne = testPerSemesterOne;
    }

    public Boolean getTestPerSemesterTwo() {
        return testPerSemesterTwo;
    }

    public void setTestPerSemesterTwo(final Boolean testPerSemesterTwo) {
        this.testPerSemesterTwo = testPerSemesterTwo;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StudyPlan plan = (StudyPlan) o;
        return gradeLevel == plan.gradeLevel &&
                Objects.equals(hoursPerSemesterOne, plan.hoursPerSemesterOne) &&
                Objects.equals(hoursPerSemesterTwo, plan.hoursPerSemesterTwo) &&
                Objects.equals(testPerSemesterOne, plan.testPerSemesterOne) &&
                Objects.equals(testPerSemesterTwo, plan.testPerSemesterTwo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gradeLevel,
                hoursPerSemesterOne,
                hoursPerSemesterTwo,
                testPerSemesterOne,
                testPerSemesterTwo);
    }
}
