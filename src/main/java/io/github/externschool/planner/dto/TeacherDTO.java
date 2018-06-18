package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TeacherDTO {

    private String verificationKey;

    @NotNull
    private String firstName;

    @NotNull
    private String patronymicName;

    @NotNull
    private String lastName;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String officer;

    private Set<SchoolSubject> schoolSubjects = new HashSet<>();

    public TeacherDTO(){

    }

    public TeacherDTO(String verificationKey, String firstName, String patronymicName,
                      String lastName, String phoneNumber, String officer,
                      Set<SchoolSubject> schoolSubjects) {
        this.verificationKey = verificationKey;
        this.firstName = firstName;
        this.patronymicName = patronymicName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.officer = officer;
        this.schoolSubjects = schoolSubjects;
    }

    public TeacherDTO constructTeacher(Teacher teacher){
        TeacherDTO teacherDTO = new TeacherDTO();

        teacherDTO.setFirstName(teacher.getFirstName());
        teacherDTO.setLastName(teacher.getLastName());
        teacherDTO.setOfficer(teacher.getOfficer());
        teacherDTO.setPatronymicName(teacher.getPatronymicName());
        teacherDTO.setPhoneNumber(teacher.getPhoneNumber());
        teacherDTO.setSchoolSubjects(teacher.getSubjectList());

        return teacherDTO;
    }

    public String getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymicName() {
        return patronymicName;
    }

    public void setPatronymicName(String patronymicName) {
        this.patronymicName = patronymicName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public Set<SchoolSubject> getSchoolSubjects() {
        return schoolSubjects;
    }

    public void setSchoolSubjects(Set<SchoolSubject> schoolSubjects) {
        this.schoolSubjects = schoolSubjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeacherDTO)) return false;
        TeacherDTO that = (TeacherDTO) o;
        return Objects.equals(verificationKey, that.verificationKey) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(patronymicName, that.patronymicName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(officer, that.officer);
    }

    @Override
    public int hashCode() {

        return Objects.hash(verificationKey, firstName, patronymicName, lastName, phoneNumber, officer);
    }
}
