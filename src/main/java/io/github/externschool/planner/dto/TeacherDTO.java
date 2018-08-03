package io.github.externschool.planner.dto;

import io.github.externschool.planner.entity.SchoolSubject;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TeacherDTO extends PersonDTO {
    @NotNull
    private String officer;

    private Set<SchoolSubject> schoolSubjects = new HashSet<>();

    public TeacherDTO() {
    }

    public TeacherDTO(final Long id,
                      final String verificationKeyValue,
                      final String firstName,
                      final String patronymicName,
                      final String lastName,
                      final String phoneNumber,
                      final String officer,
                      final Set<SchoolSubject> schoolSubjects) {
        super(id, verificationKeyValue, firstName, patronymicName, lastName, phoneNumber);
        this.officer = officer;
        this.schoolSubjects = schoolSubjects;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public Set<SchoolSubject> getSchoolSubjects() {
        return schoolSubjects;
    }

    public void setSchoolSubjects(Set<SchoolSubject> schoolSubjects) {
        this.schoolSubjects = schoolSubjects;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final TeacherDTO that = (TeacherDTO) o;
        return Objects.equals(officer, that.officer) &&
                Objects.equals(schoolSubjects, that.schoolSubjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), officer, schoolSubjects);
    }

    @Override
    public String toString() {
        return "TeacherDTO{" +
                "officer='" + officer + '\'' +
                ", schoolSubjects=" + schoolSubjects.toString() +
                '}';
    }
}
