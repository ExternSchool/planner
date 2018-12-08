package io.github.externschool.planner.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    @ManyToOne
    @JoinColumn(name = "subject", foreignKey = @ForeignKey(name = "SUBJECT_FK"))
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
        if (subject != null) {
            this.subject = subject;
            subject.addPlan(this);
        }
    }

    public void removeSubject() {
        subject.removePlan(this);
        this.subject = null;
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
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StudyPlan)) return false;

        StudyPlan studyPlan = (StudyPlan) o;

        return new EqualsBuilder()
                .append(getId(), studyPlan.getId())
                .append(getGradeLevel(), studyPlan.getGradeLevel())
                .append(getSubject(), studyPlan.getSubject())
                .append(getTitle(), studyPlan.getTitle())
                .append(getHoursPerSemesterOne(), studyPlan.getHoursPerSemesterOne())
                .append(getHoursPerSemesterTwo(), studyPlan.getHoursPerSemesterTwo())
                .append(getWorksPerSemesterOne(), studyPlan.getWorksPerSemesterOne())
                .append(getWorksPerSemesterTwo(), studyPlan.getWorksPerSemesterTwo())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getGradeLevel())
                .append(getSubject())
                .append(getTitle())
                .append(getHoursPerSemesterOne())
                .append(getHoursPerSemesterTwo())
                .append(getWorksPerSemesterOne())
                .append(getWorksPerSemesterTwo())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("gradeLevel", gradeLevel)
                .append("subject", getSubject() != null ? getSubject().getTitle() : "No Title")
                .append("title", title)
                .append("hoursPerSemesterOne", hoursPerSemesterOne)
                .append("hoursPerSemesterTwo", hoursPerSemesterTwo)
                .append("worksPerSemesterOne", worksPerSemesterOne)
                .append("worksPerSemesterTwo", worksPerSemesterTwo)
                .toString();
    }
}
