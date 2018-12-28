package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.profile.Gender;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

public class StudentDTO extends PersonDTO {
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    @NotNull private int gradeLevel;

    public StudentDTO() {
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

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(final int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StudentDTO that = (StudentDTO) o;
        return gradeLevel == that.gradeLevel &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                gender == that.gender &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateOfBirth, gender, address, gradeLevel);
    }
}
