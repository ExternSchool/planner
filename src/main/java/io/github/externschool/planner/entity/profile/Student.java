package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "student")
public class Student extends Person {

    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
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

    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Student student = (Student) o;

        if (dateOfBirth != null ? !dateOfBirth.equals(student.dateOfBirth) : student.dateOfBirth != null) return false;
        return gender == student.gender;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + gradeLevel.getValue();
        return result;
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
