package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

public class UserDTO implements Serializable {
    private Long id;
    @NotNull private String email;
    @NotNull private String password;
    @Pattern(regexp="(^$|.{36,36})", message="Key length should be 36 symbols") private String verificationKeyValue;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.verificationKeyValue = user.getVerificationKey().getValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getVerificationKeyValue() {
        return verificationKeyValue;
    }

    public void setVerificationKeyValue(final String verificationKeyValue) {
        this.verificationKeyValue = verificationKeyValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) &&
                Objects.equals(email, userDTO.email) &&
                Objects.equals(password, userDTO.password) &&
                Objects.equals(verificationKeyValue, userDTO.verificationKeyValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, verificationKeyValue);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", verificationKeyValue='" + verificationKeyValue + '\'' +
                '}';
    }
}