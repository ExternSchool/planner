package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "teacher")
public class Teacher extends Person {
    @Column(name = "officer")
    private String officer;

    @ManyToMany
    @JoinTable(name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<SchoolSubject> subjects = new HashSet<>();

    @OneToMany(mappedBy = "teacher")
    @Column(name = "courses")
    private Set<Course> courses = new HashSet<>();

    public Teacher() {
    }

    public Teacher(final Long id,
                   final String firstName,
                   final String patronymicName,
                   final String lastName,
                   final String phoneNumber,
                   final VerificationKey verificationKey,
                   final String officer,
                   final Set<SchoolSubject> subjects,
                   final Set<Course> courses) {
        super(id, firstName, patronymicName, lastName, phoneNumber);
        this.addVerificationKey(verificationKey);
        this.officer = officer;
        this.subjects = subjects;
        this.courses = courses;
    }

    public Teacher(final Person person,
                   final String officer,
                   final Set<SchoolSubject> subjects,
                   final Set<Course> courses) {
        this(person.getId(),
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
        return Collections.unmodifiableSet(subjects);
    }

    public void setSubjects(final Set<SchoolSubject> subjects) {
        this.subjects = subjects;
    }

    public void addSubject(SchoolSubject subject) {
        if (subject != null && !subjects.contains(subject)) {
            subjects.add(subject);
            subject.addTeacher(this);
        }
    }

    public void removeSubject(SchoolSubject subject) {
        if (subject != null && !subjects.isEmpty()) {
            this.subjects = subjects.stream()
                    .filter(s -> !s.getId().equals(subject.getId()))
                    .collect(Collectors.toSet());
            subject.removeTeacher(this);
        }
    }

    public Set<Course> getCourses() {
        return Collections.unmodifiableSet(courses);
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
            course.setTeacher(this);
        }
    }

    public void removeCourse(Course course) {
        if (course != null && courses.contains(course)) {
            courses.remove(course);
            course.setTeacher(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Teacher)) return false;

        Teacher teacher = (Teacher) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getOfficer(), teacher.getOfficer())
                .append(getSubjects(), teacher.getSubjects())
                .append(getCourses(), teacher.getCourses())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getOfficer())
                .append(getSubjects())
                .append(getCourses())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + getId() + '\'' +
                ", officer='" + officer + '\'' +
                '}';
    }
}
