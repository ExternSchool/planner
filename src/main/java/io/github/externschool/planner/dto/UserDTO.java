package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.User;

import javax.validation.constraints.NotNull;

public class UserDTO {

    private String verificationKey;

    @NotNull
    private String username;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String password;

    public UserDTO() {
    }

    public UserDTO(String verificationKey, String username, String phoneNumber, String password) {
        this.verificationKey = verificationKey;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public User constructUser() {
        User user = new User();
        user.setUsername(this.getUsername());
        user.setPassword(this.getPassword());
        user.setPhoneNumber(this.getPhoneNumber());
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (!username.equals(userDTO.username)) return false;
        if (!phoneNumber.equals(userDTO.phoneNumber)) return false;
        return password.equals(userDTO.password);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + phoneNumber.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}