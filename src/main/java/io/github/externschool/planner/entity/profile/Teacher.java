package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teacher")
public class Teacher extends Guest {

    @Column(name = "officer")
    private String officer;

    @ManyToMany
    @Column
    Set<SchoolSubject> subjects = new HashSet();

    public Teacher(String officer, Set<SchoolSubject> subjects) {
        this.officer= officer;
        this.subjects = subjects;
    }

    public Teacher() {
    }

    public Teacher(Long id, User user, String validationKey, String firstName, String patronymicName, String lastName,
                   String phoneNumber, String officer, Set<SchoolSubject> subjects) {
        super(id, user, validationKey, firstName, patronymicName, lastName, phoneNumber);
        this.officer = officer;
        this.subjects = subjects;
    }

    public String getOfficer() {
        return  officer;
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

    @Override
    public String toString() {
        return "Teacher{" +
                "officer='" +  + '\'' +
                ", subjects=" + subjects +
                '}';
    }
}
