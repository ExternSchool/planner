package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "student_profile")
public class StudentProfile extends Profile {

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private int yearOfStudy;


    public StudentProfile(Long id, User user, Long validationKey, String firstName,
                          String patronymicName, String lastName, String phoneNumber,
                          String password, String encryptedPassword, LocalDate dateOfBirth,
                          String gender, String address, int yearOfStudy) {
        super(id, user, validationKey, firstName, patronymicName, lastName, phoneNumber,
                password, encryptedPassword);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.yearOfStudy = yearOfStudy;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public String toString() {
        return "StudentProfile{" +
                "dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", yearOfStudy=" + yearOfStudy +
                '}';
    }


}
