package io.github.externschool.planner.entity.profile;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "school_employee_profile")
public class TeacherProfile extends GuestProfile {

    private String schoolOfficer;

    //Role role;

    //Set<Subject> subjectList = new HashSet(); // in case of teaching more than one subject

    //Set<ScheduleTime> teacherSchedule = new HashSet() // reception days/time

    //another fields



}
