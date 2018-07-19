package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.VerificationKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teacher")
public class Teacher extends Person {

    @Column(name = "officer")
    private String officer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "teacher_subjects",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<SchoolSubject> subjects = new HashSet();

    public Teacher(String officer, Set<SchoolSubject> subjects) {
        this.officer = officer;
        this.subjects = subjects;
    }

    public Teacher() {
    }

    public Teacher(final Long id,
                   final String firstName,
                   final String patronymicName,
                   final String lastName,
                   final String phoneNumber,
                   final VerificationKey verificationKey,
                   final String officer,
                   final Set<SchoolSubject> subjects) {
        super(id, firstName, patronymicName, lastName, phoneNumber, verificationKey);
        this.officer = officer;
        this.subjects = subjects;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public Set<SchoolSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<SchoolSubject> subjects) {
        this.subjects = subjects;
    }

    public void addSubject(SchoolSubject subject) {
        subjects.add(subject);
    }

    public void removeSubject(SchoolSubject subject) {
        subjects.remove(subject);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "officer='" + officer + '\'' +
                ", subjects=" + subjects +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Teacher teacher = (Teacher) o;

        return officer != null ? officer.equals(teacher.officer) : teacher.officer == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (officer != null ? officer.hashCode() : 0);
        return result;
    }
}
