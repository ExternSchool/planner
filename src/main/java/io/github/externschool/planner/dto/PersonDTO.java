package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.User;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class PersonDTO {


    private Long id;

    @NotNull
    private User user;

    @NotNull
    private String firstName;

    @NotNull
    private String patronymicName;

    @NotNull
    private String lastName;

    @NotNull
    private String phoneNumber;

    private String verificationKey;

    public PersonDTO(){

    }

    public PersonDTO(Long id, @NotNull User user, @NotNull String firstName, @NotNull String patronymicName,
                     @NotNull String lastName, @NotNull String phoneNumber, String verificationKey) {
        this.id = id;
        this.user = user;
        this.firstName = firstName;
        this.patronymicName = patronymicName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.verificationKey = verificationKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDTO)) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return Objects.equals(id, personDTO.id) &&
                Objects.equals(user, personDTO.user) &&
                Objects.equals(firstName, personDTO.firstName) &&
                Objects.equals(patronymicName, personDTO.patronymicName) &&
                Objects.equals(lastName, personDTO.lastName) &&
                Objects.equals(phoneNumber, personDTO.phoneNumber) &&
                Objects.equals(verificationKey, personDTO.verificationKey);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, user, firstName, patronymicName, lastName, phoneNumber, verificationKey);
    }
}
