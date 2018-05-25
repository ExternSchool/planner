package io.github.externschool.planner.entity.profile;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "school_employee_profile")
public class SchoolEmployeeProfile extends Profile {

    private String schoolOfficer;

    //Role role;

    //Set<Subject> subjectList = new HashSet(); // in case of teaching more than one subject

    //Set<ScheduleTime> schedule = ne HashSet() // reception days/time

    //another fields

}
