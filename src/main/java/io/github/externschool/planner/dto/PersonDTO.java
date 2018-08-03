package io.github.externschool.planner.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class PersonDTO {
    private Long id;
    private String verificationKeyValue;
    @NotNull @NotEmpty private String firstName;
    @NotNull @NotEmpty private String patronymicName;
    @NotNull @NotEmpty private String lastName;
    @NotNull @NotEmpty private String phoneNumber;

    public PersonDTO() {
    }

    public PersonDTO(final Long id,
                     final String verificationKeyValue,
                     final String firstName,
                     final String patronymicName,
                     final String lastName,
                     final String phoneNumber) {
        this.id = id;
        this.verificationKeyValue = verificationKeyValue;
        this.firstName = firstName;
        this.patronymicName = patronymicName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVerificationKeyValue() {
        return verificationKeyValue;
    }

    public void setVerificationKeyValue(final String verificationKeyValue) {
        this.verificationKeyValue = verificationKeyValue;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return Objects.equals(id, personDTO.id) &&
                Objects.equals(verificationKeyValue, personDTO.verificationKeyValue) &&
                Objects.equals(firstName, personDTO.firstName) &&
                Objects.equals(patronymicName, personDTO.patronymicName) &&
                Objects.equals(lastName, personDTO.lastName) &&
                Objects.equals(phoneNumber, personDTO.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, verificationKeyValue, firstName, patronymicName, lastName, phoneNumber);
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "id=" + (id != null ? id.toString() : "") +
                ", verificationKeyValue=" + verificationKeyValue +
                ", firstName='" + firstName + '\'' +
                ", patronymicName='" + patronymicName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
