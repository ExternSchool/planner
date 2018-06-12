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
public class Teacher extends Person {

    @Column(name = "officer")
    private String officer;

    @ManyToMany
    @Column
    Set<SchoolSubject> subjectList = new HashSet();

    public Teacher(String officer, Set<SchoolSubject> subjectList) {
        this.officer= officer;
        this.subjectList = subjectList;
    }

    public Teacher() {
    }

    public Teacher(Long id, User user, Long validationKey, String firstName, String patronymicName, String lastName,
                   String phoneNumber, String password, String encryptedPassword, String officer,
                   Set<SchoolSubject> subjectList) {
        super(id, user, validationKey, firstName, patronymicName, lastName, phoneNumber);
        this.officer = officer;
        this.subjectList = subjectList;
    }

    public String getOfficer() {
        return  officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public Set<SchoolSubject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(Set<SchoolSubject> subjectList) {
        this.subjectList = subjectList;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "officer='" +  + '\'' +
                ", subjectList=" + subjectList +
                '}';
    }
}
