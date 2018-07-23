package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.profile.Teacher;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "school_subject")
public class SchoolSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "subjects", fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    @Column(name = "teacher_id")
    private Set<Teacher> teachers = new HashSet<>();

    @OneToMany(mappedBy = "subject")
    @Column(name = "plans")
    private Set<StudyPlan> plans = new HashSet<>();

    public SchoolSubject() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Teacher> getTeachers() {
        return teachers;

    public Set<StudyPlan> getPlans() {
        return plans;
    }

    public void setPlans(final Set<StudyPlan> plans) {
        this.plans = plans;
    }

    public void addPlan(StudyPlan plan) {
        plans.add(plan);
        plan.setSubject(this);
    }

    public void removePlan(StudyPlan plan) {
        plans.remove(plan);
        plan.setSubject(null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SchoolSubject subject = (SchoolSubject) o;
        return Objects.equals(name, subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "SchoolSubject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
