package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Gender;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

public class StudentDTO {
    private Long id;
    @NotNull
    private VerificationKey verificationKey;
    @NotNull
    private String firstName;
    @NotNull
    private String patronymicName;
    @NotNull
    private String lastName;
    @NotNull
    private String phoneNumber;
    @NotNull
    private LocalDate dateOfBirth;
    @NotNull
    private Gender gender;
    @NotNull
    private String address;
    @NotNull
    private int gradeLevel;

    public StudentDTO() {
    }

    public StudentDTO(final Long id,
                      final VerificationKey verificationKey,
                      final String firstName,
                      final String patronymicName,
                      final String lastName,
                      final String phoneNumber,
                      final LocalDate dateOfBirth,
                      final Gender gender,
                      final String address,
                      final int gradeLevel) {
        this.id = id;
        this.verificationKey = verificationKey;
        this.firstName = firstName;
        this.patronymicName = patronymicName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.gradeLevel = gradeLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VerificationKey getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(VerificationKey verificationKey) {
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDTO that = (StudentDTO) o;
        return gradeLevel == that.gradeLevel &&
                Objects.equals(id, that.id) &&
                Objects.equals(verificationKey, that.verificationKey) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(patronymicName, that.patronymicName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                gender == that.gender &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, verificationKey, firstName, patronymicName, lastName, phoneNumber, dateOfBirth, gender, address, gradeLevel);
    }
}
