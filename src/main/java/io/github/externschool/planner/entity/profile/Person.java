package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.VerificationKey;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id")
    private VerificationKey verificationKey;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "patronymic_name")
    private String patronymicName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    public Person() {
    }

    public Person(final Long id,
                  final String firstName,
                  final String patronymicName,
                  final String lastName,
                  final String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.patronymicName = patronymicName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymicName() {
        return patronymicName;
    }

    public void setPatronymicName(String patronymicName) {
        this.patronymicName = patronymicName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getShortName() {
        return lastName + " " +
                (firstName != null
                        ? (!firstName.isEmpty() ? firstName.substring(0,1) + "." : "")
                        : "") +
                (patronymicName != null
                        ? (!patronymicName.isEmpty() ? patronymicName.substring(0,1) + "." : "")
                        : "");
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public VerificationKey getVerificationKey() {
        return verificationKey;
    }

    public void addVerificationKey(VerificationKey verificationKey) {
        this.verificationKey = verificationKey;
        if (verificationKey != null) {
            verificationKey.setPerson(this);
        }
    }

    public void removeVerificationKey() {
        if (verificationKey != null) {
            verificationKey.setPerson(null);
        }
        this.verificationKey = null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("verificationKey", verificationKey)
                .append("firstName", firstName)
                .append("patronymicName", patronymicName)
                .append("lastName", lastName)
                .append("phoneNumber", phoneNumber)
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        return new EqualsBuilder()
                .append(getId(), person.getId())
                .append(getVerificationKey(), person.getVerificationKey())
                .append(getFirstName(), person.getFirstName())
                .append(getPatronymicName(), person.getPatronymicName())
                .append(getLastName(), person.getLastName())
                .append(getPhoneNumber(), person.getPhoneNumber())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getVerificationKey())
                .append(getFirstName())
                .append(getPatronymicName())
                .append(getLastName())
                .append(getPhoneNumber())
                .toHashCode();
    }
}
