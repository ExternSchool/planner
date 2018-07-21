package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.course.Course;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teacher")
public class Teacher extends Person {

    @Column(name = "officer")
    private String officer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "teacher_subjects",
            joinColumns = @JoinColumn(name = "teacher"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<SchoolSubject> subjects = new HashSet();

    @OneToMany(mappedBy = "teacher")
    @Column(name = "courses")
    private Set<Course> courses = new HashSet<>();

    public Teacher() {
    }

    public Teacher(final Long id,
                   final User user,
                   final String firstName,
                   final String patronymicName,
                   final String lastName,
                   final String phoneNumber,
                   final String verificationKey,
                   final String officer,
                   final Set<SchoolSubject> subjects,
                   final Set<Course> courses) {
        super(id, user, firstName, patronymicName, lastName, phoneNumber, verificationKey);
        this.officer = officer;
        this.subjects = subjects;
        this.courses = courses;
    }

    public Teacher(final Person person,
                   final String officer,
                   final Set<SchoolSubject> subjects,
                   final Set<Course> courses) {
        this(person.getId(),
                person.getUser(),
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName(),
                person.getPhoneNumber(),
                person.getVerificationKey(),
                officer,
                subjects,
                courses);
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public Set<SchoolSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<SchoolSubject> subjects) {
        this.subjects = subjects;
    }

    public void addSubject(SchoolSubject subject) {
        subjects.add(subject);
    }

    public void removeSubject(SchoolSubject subject) {
        subjects.remove(subject);
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(final Set<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.setTeacher(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.setTeacher(null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final Teacher teacher = (Teacher) o;
        return Objects.equals(officer, teacher.officer) &&
                Objects.equals(subjects, teacher.subjects) &&
                Objects.equals(courses, teacher.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), officer, subjects, courses);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + getId() + '\'' +
                ", officer='" + officer + '\'' +
                '}';
    }
}
