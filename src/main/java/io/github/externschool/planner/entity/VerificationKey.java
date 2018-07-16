package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.profile.Person;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "verificationKey")
public class VerificationKey {
    @Id
    @Column(name = "name")
    private String name = UUID.randomUUID().toString();

    @OneToOne
    private User user;

    @OneToOne
    private Person person;

    public VerificationKey() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationKey that = (VerificationKey) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(user, that.user) &&
                Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, user, person);
    }
}
