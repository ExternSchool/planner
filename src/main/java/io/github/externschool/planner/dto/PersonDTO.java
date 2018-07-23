package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.VerificationKey;

import javax.validation.constraints.NotNull;

public class PersonDTO {
    private Long id;
    private VerificationKey verificationKey;
    @NotNull
    private String firstName;
    @NotNull
    private String patronymicName;
    @NotNull
    private String lastName;
    @NotNull
    private String phoneNumber;

    public PersonDTO() {
    }

    public PersonDTO(final Long id,
                     final VerificationKey verificationKey,
                     final String firstName,
                     final String patronymicName,
                     final String lastName,
                     final String phoneNumber) {
        this.id = id;
        this.verificationKey = verificationKey;
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
}
