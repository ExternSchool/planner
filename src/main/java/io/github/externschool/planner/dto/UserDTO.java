package io.github.externschool.planner.dto;

import javax.validation.constraints.NotNull;

public class UserDTO {

    // TODO: decide if we could use it to link a real person data for students and teachers (null for a guest)
    private String identityKey;

    // TODO: could we use email as username to keep it simple?
    @NotNull
    private String username;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String password;

    public UserDTO() {
    }

    public UserDTO(String identityKey, String username, String phoneNumber, String password, String encryptedPassword) {
        this.identityKey = identityKey;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getIdentityKey() {
        return identityKey;
    }

    public void setIdentityKey(String identityKey) {
        this.identityKey = identityKey;
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
                "identityKey='" + identityKey + '\'' +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (identityKey != null ? !identityKey.equals(userDTO.identityKey) : userDTO.identityKey != null) return false;
        if (!username.equals(userDTO.username)) return false;
        if (!phoneNumber.equals(userDTO.phoneNumber)) return false;
        return password.equals(userDTO.password);
    }

    @Override
    public int hashCode() {
        int result = identityKey != null ? identityKey.hashCode() : 0;
        result = 31 * result + username.hashCode();
        result = 31 * result + phoneNumber.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
