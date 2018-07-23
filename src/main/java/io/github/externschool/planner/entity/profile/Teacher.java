package io.github.externschool.planner.entity.profile;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teacher")
public class Teacher extends Person {

    @Column(name = "officer")
    private String officer;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinTable(name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<SchoolSubject> subjects = new HashSet();

    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
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
        return subjects;
    }

    public void addSubject(SchoolSubject subject) {
        if (subject != null && !subjects.contains(subject)) {
            subjects.add(subject);
            subject.getTeachers().add(this);
        }
    }

    public void removeSubject(SchoolSubject subject) {
        if (subject != null && subjects.contains(subject)) {
            subjects.remove(subject);
            subject.getTeachers().remove(this);
        }
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(final Set<Course> courses) {
        this.courses = courses;
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
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Teacher teacher = (Teacher) o;

        return officer != null ? officer.equals(teacher.officer) : teacher.officer == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (officer != null ? officer.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + getId() + '\'' +
                ", officer='" + officer + '\'' +
                '}';
    }
}
