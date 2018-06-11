package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "student")
public class Student extends Person {

    private LocalDate dateOfBirth;

    private Gender gender;

    private String address;

    private int yearOfStudy;

    public Student(){

    }

    public Student(Long id, User user, Long validationKey, String firstName, String patronymicName,
                   String lastName, String phoneNumber, String password, String encryptedPassword,
                   LocalDate dateOfBirth, Gender gender, String address, int yearOfStudy) {
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

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public String toString() {
        return "Student{" +
                "dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", yearOfStudy=" + yearOfStudy +
                '}';
    }


}
