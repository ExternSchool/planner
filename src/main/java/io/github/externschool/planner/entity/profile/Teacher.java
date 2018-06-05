package io.github.externschool.planner.entity.profile;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "teacher")
public class Teacher extends Person {

    private String schoolOfficer;

//    Set<Subject> subjectList = new HashSet();
//
//
//    public Teacher(String schoolOfficer, Set<Subject> subjectList) {
//        this.schoolOfficer = schoolOfficer;
//        this.subjectList = subjectList;
//    }
//
//    public Teacher(Long id, User user, Long validationKey, String firstName, String patronymicName, String lastName, String phoneNumber, String password, String encryptedPassword, String schoolOfficer, Set<Subject> subjectList) {
//        super(id, user, validationKey, firstName, patronymicName, lastName, phoneNumber, password, encryptedPassword);
//        this.schoolOfficer = schoolOfficer;
//        this.subjectList = subjectList;
//    }
//
//    public String getSchoolOfficer() {
//        return schoolOfficer;
//    }
//
//    public void setSchoolOfficer(String schoolOfficer) {
//        this.schoolOfficer = schoolOfficer;
//    }
//
//    public Set<Subject> getSubjectList() {
//        return subjectList;
//    }
//
//    public void setSubjectList(Set<Subject> subjectList) {
//        this.subjectList = subjectList;
//    }
//
//    @Override
//    public String toString() {
//        return "Teacher{" +
//                "schoolOfficer='" + schoolOfficer + '\'' +
//                ", subjectList=" + subjectList +
//                '}';
//    }
}
