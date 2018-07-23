package io.github.externschool.planner.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject", foreignKey = @ForeignKey(name = "SUBJECT_FK"))
    @Cascade(CascadeType.SAVE_UPDATE)
    private SchoolSubject subject;

    private String name;

    @Column(name = "hrs_s1")
    private Integer hoursPerSemesterOne;
    @Column(name = "hrs_s2")
    private Integer hoursPerSemesterTwo;
    @Column(name = "exam_s1")
    private Boolean examSemesterOne;
    @Column(name = "exam_s2")
    private Boolean examSemesterTwo;

    public StudyPlan() {
    }

    public StudyPlan(final GradeLevel gradeLevel, final SchoolSubject subject) {
        this.gradeLevel = gradeLevel;
        this.subject = subject;
        this.name = subject.getName();
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public Boolean getExamSemesterOne() {
        return examSemesterOne;
    }

    public void setExamSemesterOne(final Boolean examSemesterOne) {
        this.examSemesterOne = examSemesterOne;
    }

    public Boolean getExamSemesterTwo() {
        return examSemesterTwo;
    }

    public void setExamSemesterTwo(final Boolean examSemesterTwo) {
        this.examSemesterTwo = examSemesterTwo;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StudyPlan plan = (StudyPlan) o;
        return gradeLevel == plan.gradeLevel &&
                Objects.equals(hoursPerSemesterOne, plan.hoursPerSemesterOne) &&
                Objects.equals(hoursPerSemesterTwo, plan.hoursPerSemesterTwo) &&
                Objects.equals(examSemesterOne, plan.examSemesterOne) &&
                Objects.equals(examSemesterTwo, plan.examSemesterTwo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gradeLevel,
                hoursPerSemesterOne,
                hoursPerSemesterTwo,
                examSemesterOne,
                examSemesterTwo);
    }

    @Override
    public String toString() {
        return "StudyPlan{" +
                "id=" + id +
                ", name=" + name +
                ", gradeLevel=" + gradeLevel +
                ", subject=" + subject +
                ", hrsOne=" + hoursPerSemesterOne +
                ", hrsTwo=" + hoursPerSemesterTwo +
                ", examOne=" + examSemesterOne +
                ", examTwo=" + examSemesterTwo +
                '}';
    }
}
