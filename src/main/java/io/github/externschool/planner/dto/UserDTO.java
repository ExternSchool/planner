package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.VerificationKey;

import javax.validation.constraints.NotNull;

public class UserDTO {

    private Long id;

    private VerificationKey verificationKey;

    @NotNull
    private String email;

    @NotNull
    private String password;

    public UserDTO() {
    }

    public UserDTO(Long id, VerificationKey verificationKey,
                   @NotNull String email, @NotNull String password) {
        this.id = id;
        this.verificationKey = verificationKey;
        this.email = email;
        this.password = password;
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

    public void setEmail(String email) {
        this.email = email;
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
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (!email.equals(userDTO.email)) return false;
        return password.equals(userDTO.password);
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}