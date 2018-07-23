package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.SchoolSubject;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teacher")
public class Teacher extends Person {

    @Column(name = "officer")
    private String officer;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinTable(name = "teacher_subject",
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
                   final String officer,
                   final Set<SchoolSubject> subjects) {
        super(id, firstName, patronymicName, lastName, phoneNumber);
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

    public void addSubject(SchoolSubject subject) {
        if (subject != null && !subjects.contains(subject)) {
            subjects.add(subject);
            subject.getTeachers().add(this);
        }
    }

    public void removeSubject(SchoolSubject subject) {
        if (subject != null && subjects.contains(subject)) {
            subjects.remove(subject);
            subject.getTeachers().remove(this);
        }
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
