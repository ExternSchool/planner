package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.VerificationKey;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "student")
public class Student extends Person {
    @Column(name = "grade_level")
    @Enumerated(EnumType.ORDINAL)
    private GradeLevel gradeLevel;

    public Student() {
    }

    public Student(final Long id,
                   final String firstName,
                   final String patronymicName,
                   final String lastName,
                   final String phoneNumber,
                   final VerificationKey verificationKey,
                   final LocalDate dateOfBirth,
                   final Gender gender,
                   final String address,
                   final GradeLevel gradeLevel) {
        super(id, firstName, patronymicName, lastName, phoneNumber);
        this.addVerificationKey(verificationKey);
        this.gradeLevel = gradeLevel;
    }

    public Student(final Person person,
                   final LocalDate dateOfBirth,
                   final Gender gender,
                   final String address,
                   final GradeLevel gradeLevel) {
        this(person.getId(),
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
        return Objects.equals(this.getId(), student.getId()) && gradeLevel == student.gradeLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gradeLevel);
    }

    @Override
    public String toString() {
        return "Student{" +
                "personId=" + this.getId() + '\'' +
                ", gradeLevel=" + gradeLevel +
                '}';
    }
}
