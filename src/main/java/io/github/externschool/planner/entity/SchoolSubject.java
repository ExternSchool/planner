package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.profile.Teacher;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
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

    @ManyToMany
    @Column(name = "teachers")
    private Set<Teacher> teachers = new HashSet<>();

    @ManyToMany(mappedBy = "subjects")
    @Column(name = "grades")
    private Set<Grade> grades = new HashSet<>();

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SchoolSubject subject = (SchoolSubject) o;

        return id.equals(subject.id);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
