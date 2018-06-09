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
    @Column(name = "subject_id", nullable = false
    , updatable = false)
    private Long id;

    @Column(name = "subject_name")
    private String subjectName;

    @ManyToMany
    @Column
    private Set<Teacher> teachers = new HashSet<>();

    public SchoolSubject(Long id, String subjectName, Set<Teacher> teachers) {
        this.id = id;
        this.subjectName = subjectName;
        this.teachers = teachers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }

    @Override
    public String toString() {
        return "SchoolSubject{" +
                "id=" + id +
                ", subjectName='" + subjectName + '\'' +
                ", teachers=" + teachers +
                '}';
    }
}
