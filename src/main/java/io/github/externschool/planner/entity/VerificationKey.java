package io.github.externschool.planner.entity;

import io.github.externschool.planner.entity.profile.Person;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "verificationKey")
public class VerificationKey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "value")
    private final String value = UUID.randomUUID().toString();

    @OneToOne(mappedBy = "verificationKey", fetch = FetchType.EAGER)
    private User user;

    @OneToOne(mappedBy = "verificationKey", fetch = FetchType.EAGER)
    private Person person;

    public VerificationKey() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VerificationKey key = (VerificationKey) o;
        return Objects.equals(id, key.id) &&
                Objects.equals(value, key.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}
