package io.github.externschool.planner.entity;

import io.github.externschool.planner.dto.UserDTO;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "username")
    private String username;

    @Transient
    @Column(name = "password")
    private String password;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

    public User(){

    }

    public User(String phoneNumber, String username, String password, String encryptedPassword) {
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.encryptedPassword = encryptedPassword;
    }

    public UserDTO constructUser() {
        UserDTO useDtO = new UserDTO();
        useDtO.setUsername(this.getUsername());
        useDtO.setPassword(this.getPassword());
        useDtO.setPhoneNumber(this.getPhoneNumber());
        return  useDtO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(encryptedPassword, user.encryptedPassword);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, phoneNumber, username, password, encryptedPassword);
    }
}
