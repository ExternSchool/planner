package io.github.externschool.planner.entity;

import io.github.externschool.planner.dto.UserDTO;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Transient
    @Column(name = "password")
    private String password;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "name")
    )
    private List<Authority> authorities = new ArrayList<>();

    public User(){
    }

    public User(String phoneNumber, String email, String password, String encryptedPassword) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.encryptedPassword = encryptedPassword;
    }

    public void addAuthority(Authority authority) {
        authorities.add(authority);
        authority.getUsers().add(this);
    }

    public void removeAuthority(Authority authority) {
        authorities.remove(authority);
        authority.getUsers().remove(this);
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
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(encryptedPassword, user.encryptedPassword);
    }

    public UserDTO constructUser() {
        UserDTO useDtO = new UserDTO();
        useDtO.setEmail(this.getEmail());
        useDtO.setPassword(this.getPassword());
        useDtO.setPhoneNumber(this.getPhoneNumber());
        return  useDtO;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, phoneNumber, email, password, encryptedPassword);
    }
}
