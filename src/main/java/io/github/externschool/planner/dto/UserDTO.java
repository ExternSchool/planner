package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.User;

import javax.validation.constraints.NotNull;

public class UserDTO {

    private String verificationKey;

    @NotNull
    private String email;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String password;

    public UserDTO() {
    }

    public UserDTO(String verificationKey, String email, String phoneNumber, String password) {
        this.verificationKey = verificationKey;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
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

    @Override
    public String toString() {
        return "User{" +
                "verificationKey='" + verificationKey + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    //TODO Move to UserDtoToUserConverter
    public User constructUser() {
        User user = new User();
        user.setEmail(this.getEmail());
        user.setPassword(this.getPassword());
        return user;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (!email.equals(userDTO.email)) return false;
        if (!phoneNumber.equals(userDTO.phoneNumber)) return false;
        return password.equals(userDTO.password);
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + phoneNumber.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}