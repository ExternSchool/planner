package io.github.externschool.planner.entity;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.profile.Person;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Person person;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "email")
    private String email;

    @Transient
    private String password;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

    public User() {
    }

    public User(Person person, String email, String password, String encryptedPassword) {
        this.person = person;
        this.email = email;
        this.password = password;
        this.encryptedPassword = encryptedPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public Collection<Role> getRoles() {
        return roles;
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
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(encryptedPassword, user.encryptedPassword) &&
                Objects.equals(roles, user.roles);
    }

    //TODO Move to UserToUserDtoConverter
    public UserDTO constructUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(this.getEmail());
        userDTO.setPassword(this.getPassword());
        return userDTO;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, encryptedPassword, roles);
    }
}
