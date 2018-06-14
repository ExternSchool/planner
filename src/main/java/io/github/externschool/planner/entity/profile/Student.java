package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "student")
public class Student extends Guest {

    private LocalDate dateOfBirth;

    private Gender gender;

    private String address;

    private int gradeLevel;

    public Student(){
    }

    public Student(Long id, User user, String validationKey, String firstName, String patronymicName,
                   String lastName, String phoneNumber,
                   LocalDate dateOfBirth, Gender gender, String address, int gradeLevel) {

        super(id, user, validationKey, firstName, patronymicName, lastName, phoneNumber);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.gradeLevel = gradeLevel;
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
    public String toString() {
        return "Student{" +
                "dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", gradeLevel=" + gradeLevel +
                '}';
    }
}
