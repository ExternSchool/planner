package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "student")
public class Student extends Person {

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private Gender gender;

    @Column(name = "address")
    private String address;

    @Column(name = "grade_level")
    @Enumerated
    private GradeLevel gradeLevel;

    public Student(){
    }

    public Student(final Long id,
                   final User user,
                   final String firstName,
                   final String patronymicName,
                   final String lastName,
                   final String phoneNumber,
                   final String verificationKey,
                   final LocalDate dateOfBirth,
                   final Gender gender,
                   final String address,
                   final GradeLevel gradeLevel) {
        super(id, user, firstName, patronymicName, lastName, phoneNumber, verificationKey);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.gradeLevel = gradeLevel;
    }

    public Student(final Person person,
                   final LocalDate dateOfBirth,
                   final Gender gender,
                   final String address,
                   final GradeLevel gradeLevel) {
        this(person.getId(),
                person.getUser(),
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName(),
                person.getPhoneNumber(),
                person.getVerificationKey(),
                dateOfBirth,
                gender,
                address,
                gradeLevel);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(final GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final Student student = (Student) o;
        return Objects.equals(dateOfBirth, student.dateOfBirth) &&
                gender == student.gender &&
                Objects.equals(address, student.address) &&
                gradeLevel == student.gradeLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateOfBirth, gender, address, gradeLevel);
    }

    @Override
    public String toString() {
        return "Student{" +
                "dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", address='" + address + '\'' +
                ", gradeLevel=" + gradeLevel +
                '}';
    }
}
