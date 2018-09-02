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

    private String title;

    @Column(name = "hrs_s1")
    private Integer hoursPerSemesterOne;
    @Column(name = "hrs_s2")
    private Integer hoursPerSemesterTwo;
    @Column(name = "exam_s1")
    private Integer worksPerSemesterOne;
    @Column(name = "exam_s2")
    private Integer worksPerSemesterTwo;

    public StudyPlan() {
    }

    public StudyPlan(final GradeLevel gradeLevel, final SchoolSubject subject) {
        this.gradeLevel = gradeLevel;
        this.subject = subject;
        this.title = subject.getTitle();
    }

    public StudyPlan(GradeLevel gradeLevel,
                     SchoolSubject subject,
                     String title,
                     Integer hoursPerSemesterOne,
                     Integer hoursPerSemesterTwo,
                     Integer worksPerSemesterOne,
                     Integer worksPerSemesterTwo) {
        this.gradeLevel = gradeLevel;
        this.subject = subject;
        this.title = title;
        this.hoursPerSemesterOne = hoursPerSemesterOne;
        this.hoursPerSemesterTwo = hoursPerSemesterTwo;
        this.worksPerSemesterOne = worksPerSemesterOne;
        this.worksPerSemesterTwo = worksPerSemesterTwo;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StudyPlan plan = (StudyPlan) o;
        return Objects.equals(id, plan.id) &&
                gradeLevel == plan.gradeLevel &&
                Objects.equals(subject, plan.subject) &&
                Objects.equals(title, plan.title) &&
                Objects.equals(hoursPerSemesterOne, plan.hoursPerSemesterOne) &&
                Objects.equals(hoursPerSemesterTwo, plan.hoursPerSemesterTwo) &&
                Objects.equals(worksPerSemesterOne, plan.worksPerSemesterOne) &&
                Objects.equals(worksPerSemesterTwo, plan.worksPerSemesterTwo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gradeLevel,
                hoursPerSemesterOne,
                hoursPerSemesterTwo,
                worksPerSemesterOne,
                worksPerSemesterTwo);
    }

    @Override
    public String toString() {
        return "StudyPlan{" +
                "id=" + id +
                ", title=" + title +
                ", gradeLevel=" + gradeLevel +
                ", subject=" + subject +
                ", hrsOne=" + hoursPerSemesterOne +
                ", hrsTwo=" + hoursPerSemesterTwo +
                ", examOne=" + worksPerSemesterOne +
                ", examTwo=" + worksPerSemesterTwo +
                '}';
    }
}
