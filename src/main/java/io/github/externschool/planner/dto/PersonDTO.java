package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.VerificationKey;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class PersonDTO {
    private Long id;
    private VerificationKey verificationKey;
    private String email;
    @NotBlank private String firstName;
    @NotBlank private String patronymicName;
    @NotBlank private String lastName;
    @NotBlank private String phoneNumber;
    private String optionalData;

    public PersonDTO() {
    }

    public PersonDTO(final Long id,
                     final VerificationKey verificationKey,
                     final String email,
                     final String firstName,
                     final String patronymicName,
                     final String lastName,
                     final String phoneNumber) {
        this.id = id;
        this.verificationKey = verificationKey;
        this.email = email;
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

    public VerificationKey getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(VerificationKey verificationKey) {
        this.verificationKey = verificationKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
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

    public String getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(final String optionalData) {
        this.optionalData = optionalData;
    }
    
    public String getShortName() {
        return lastName + " " +
                (firstName != null ? firstName.substring(0,1) + "." : "") +
                (patronymicName != null ? patronymicName.substring(0,1) + "." : "");
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
                Objects.equals(verificationKey, personDTO.verificationKey) &&
                Objects.equals(email, personDTO.email) &&
                Objects.equals(firstName, personDTO.firstName) &&
                Objects.equals(patronymicName, personDTO.patronymicName) &&
                Objects.equals(lastName, personDTO.lastName) &&
                Objects.equals(phoneNumber, personDTO.phoneNumber) &&
                Objects.equals(optionalData, personDTO.optionalData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, verificationKey, email, firstName, patronymicName, lastName, phoneNumber, optionalData);
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "id=" + id +
                ", verificationKey=" + (verificationKey != null ? verificationKey : "") +
                ", firstName='" + firstName + '\'' +
                ", patronymicName='" + patronymicName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
