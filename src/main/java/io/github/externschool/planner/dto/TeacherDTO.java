package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.profile.Teacher;

import javax.validation.constraints.NotNull;
import java.util.Objects;

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
    private String email;

    @NotNull
    private String officer;

    private String schoolSubject;

    public TeacherDTO(){

    }

    public TeacherDTO(String verificationKey, String firstName, String patronymicName, String lastName,
                      String phoneNumber, String email, String officer, String schoolSubject) {
        this.verificationKey = verificationKey;
        this.firstName = firstName;
        this.patronymicName = patronymicName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.officer = officer;
        this.schoolSubject = schoolSubject;
    }

    public Teacher constructTeacher(){

        Teacher teacher = new Teacher();
        teacher.setFirstName(this.getFirstName());
        teacher.setLastName(this.getLastName());
        teacher.setOfficer(this.getOfficer());
        teacher.setPatronymicName(this.getPatronymicName());
        teacher.setPhoneNumber(this.getPhoneNumber());

        return teacher;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public String getSchoolSubject() {
        return schoolSubject;
    }

    public void setSchoolSubject(String schoolSubject) {
        this.schoolSubject = schoolSubject;
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
                Objects.equals(email, that.email) &&
                Objects.equals(officer, that.officer) &&
                Objects.equals(schoolSubject, that.schoolSubject);
    }

    @Override
    public int hashCode() {

        return Objects.hash(verificationKey, firstName, patronymicName, lastName, phoneNumber, email, officer,
                schoolSubject);
    }
}
