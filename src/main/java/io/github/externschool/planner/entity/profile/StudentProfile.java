package io.github.externschool.planner.entity.profile;

import java.time.LocalDate;

public class StudentProfile extends GuestProfile {

    private LocalDate dateOfBirth;

    private String gender;

    private String adress;

    private int yearOfStudy;


    public StudentProfile() {
    }

    public StudentProfile(Long id, Long validationKey, String firstName, String patronymicName,
                          String lastName, String email, String phoneNumber, String password,
                          String encryptedPassword, LocalDate dateOfBirth, String gender, String adress,
                          int yearOfStudy) {
        super(id, validationKey, firstName, patronymicName, lastName, email, phoneNumber,
                password, encryptedPassword);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.adress = adress;
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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
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
                ", adress='" + adress + '\'' +
                ", yearOfStudy=" + yearOfStudy +
                '}';
    }
}
