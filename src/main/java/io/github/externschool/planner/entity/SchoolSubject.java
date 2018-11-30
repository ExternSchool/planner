package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.profile.Teacher;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "school_subject")
public class SchoolSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToMany(mappedBy = "subjects")
    @Column(name = "teacher_id")
    private Set<Teacher> teachers = new HashSet<>();

    @OneToMany(mappedBy = "subject")
    @Column(name = "plan_id")
    private Set<StudyPlan> plans = new HashSet<>();

    public SchoolSubject() {
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Set<Teacher> getTeachers() {
        return Collections.unmodifiableSet(teachers);
    }

    // use Teacher.addSubject()
    public void addTeacher(final Teacher teacher) {
        if (teacher != null && !teachers.contains(teacher)) {
            this.teachers.add(teacher);
        }
    }

    // use Teacher.removeSubject()
    public void removeTeacher(final Teacher teacher) {
        if (teacher != null && !teachers.isEmpty()) {
            this.teachers = teachers.stream()
                    .filter(t -> !t.getId().equals(teacher.getId()))
                    .collect(Collectors.toSet());
        }
    }

    public Set<StudyPlan> getPlans() {
        return Collections.unmodifiableSet(plans);
    }

    // use StudyPlan.setSubject()
    void addPlan(final StudyPlan plan) {
        if (plan != null && !plans.contains(plan)) {
            this.plans.add(plan);
        }
    }

    // use StudyPlan.removeSubject()
    void removePlan(final StudyPlan plan) {
        if (plan != null && !plans.isEmpty()) {
            this.plans = plans.stream()
                    .filter(p -> !p.getId().equals(plan.getId()))
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SchoolSubject)) return false;

        SchoolSubject subject = (SchoolSubject) o;

        return new EqualsBuilder()
                .append(getId(), subject.getId())
                .append(getTitle(), subject.getTitle())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getTitle())
                .toHashCode();
    }

    @Override
    public String toString() {
        //Please do not change - parsing with SchoolSubjectFormatter
        return id != null ? id.toString() : "" + (title != null ? " " + title : "");
    }
}
