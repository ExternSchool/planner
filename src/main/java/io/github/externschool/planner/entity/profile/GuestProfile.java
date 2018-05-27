package io.github.externschool.planner.entity.profile;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "quest_profile")
@Inheritance(strategy = InheritanceType.JOINED)
public class GuestProfile {

    @Id
    @Column(name = "guest_profile_id")
    private Long id;

    private Long validationKey;

    private String firstName;

    private String patronymicName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String password;

    private String encryptedPassword;


    public GuestProfile(){

    }

    public GuestProfile(Long id, Long validationKey, String firstName, String patronymicName, String lastName,
                        String email, String phoneNumber, String password, String encryptedPassword) {
        this.id = id;
        this.validationKey = validationKey;
        this.firstName = firstName;
        this.patronymicName = patronymicName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.encryptedPassword = encryptedPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getValidationKey() {
        return validationKey;
    }

    public void setValidationKey(Long validationKey) {
        this.validationKey = validationKey;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    @Override
    public String toString() {
        return "GuestProfile{" +
                "id=" + id +
                ", validationKey=" + validationKey +
                ", firstName='" + firstName + '\'' +
                ", patronymicName='" + patronymicName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuestProfile)) return false;
        GuestProfile that = (GuestProfile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(validationKey, that.validationKey) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(patronymicName, that.patronymicName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(password, that.password) &&
                Objects.equals(encryptedPassword, that.encryptedPassword);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, validationKey, firstName, patronymicName, lastName, email, phoneNumber,
                password, encryptedPassword);
    }
}
