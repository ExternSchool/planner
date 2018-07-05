package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.GradeLevel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "grade")
public class Grade {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "grade_subjects",
            joinColumns = @JoinColumn(name = "grade_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<SchoolSubject> subjects = new HashSet<>();

    private Grade() {}

    public Grade(final GradeLevel gradeLevel, final String name, final Set<SchoolSubject> subjects) {
        this.id = gradeLevel.getValue();
        this.name = name;
        this.subjects = subjects;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public GradeLevel getLevel() {
        return GradeLevel.values()[id];
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<SchoolSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(final Set<SchoolSubject> subjects) {
        this.subjects = subjects;
    }

    public Set<SchoolSubject> addSubject(SchoolSubject subject) {
        subjects.add(subject);

        return subjects;
    }

    public Set<SchoolSubject> removeSubject(SchoolSubject subject) {
        subjects.remove(subject);

        return subjects;
    }
}
