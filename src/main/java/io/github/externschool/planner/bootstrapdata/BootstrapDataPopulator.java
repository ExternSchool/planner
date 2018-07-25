package io.github.externschool.planner.bootstrapdata;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@ExcludeFromTests
public class BootstrapDataPopulator implements InitializingBean {
    private final UserService userService;
    private final TeacherService teacherService;
    private final SchoolSubjectService schoolSubjectService;
    private final ScheduleEventTypeRepository eventTypeRepository;
    private final VerificationKeyService verificationKeyService;

    public BootstrapDataPopulator(final UserService userService,
                                  final TeacherService teacherService,
                                  final SchoolSubjectService schoolSubjectService,
                                  final ScheduleEventTypeRepository eventTypeRepository
                                  final VerificationKeyService verificationKeyService) {                                  
        this.userService = userService;
        this.teacherService = teacherService;
        this.schoolSubjectService = schoolSubjectService;
        this.verificationKeyService = verificationKeyService;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createScheduleEventType();
      
        VerificationKey key = new VerificationKey();
        verificationKeyService.saveOrUpdateKey(key);
        User user = userService.createUser("q@q", "q", "ROLE_ADMIN");
        user.addVerificationKey(key);
        userService.saveOrUpdate(user);

        Teacher teacher = createTeacher(new Person(
                        null,
                        "James",
                        "Merrill",
                        "Carlsmith",
                        "(066)666-6666"),
                "Psychologist",
                Collections.singletonList("Cognitive dissonance theory"));
        teacher.addVerificationKey(key);
        teacherService.saveOrUpdateTeacher(teacher);

        teacher = createTeacher(new Person(
                        null,
                        "Alexander",
                        "Sutherland",
                        "Neill",
                        "(099)999-9999"),
                "Principal",
                Arrays.asList("Quantum Mechanics", "Algebraic topology"));
        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        teacher.addVerificationKey(key);
        teacherService.saveOrUpdateTeacher(teacher);

        teacher = createTeacher(new Person(
                        null,
                        "Leonardo",
                        "di ser Piero",
                        "da Vinci",
                        "(099)999-1111"),
                "Teacher",
                Arrays.asList("Anatomy and physiology", "Rocket Science"));
        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        teacher.addVerificationKey(key);
        teacherService.saveOrUpdateTeacher(teacher);
    }

    private Teacher createTeacher(Person person, String officerName, List<String> subjectsNames) {
        Teacher teacher = new Teacher(
                person.getId(),
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName(),
                person.getPhoneNumber(),
                officerName,
                new HashSet<>());
        teacherService.saveOrUpdateTeacher(teacher);

        for (String subjectName : subjectsNames) {
            SchoolSubject subject = new SchoolSubject();
            subject.setName(subjectName);
            StudyPlan plan = new StudyPlan(GradeLevel.LEVEL_7, subject);
            plan.setHoursPerSemesterOne(2);
            plan.setHoursPerSemesterTwo(2);
            plan.setExamSemesterOne(true);
            plan.setExamSemesterTwo(true);
            subject.addPlan(plan);
            schoolSubjectService.saveOrUpdateSubject(subject);
            teacher.addSubject(subject);
        }

        return teacher;
    }
    
    private void createScheduleEventType() {
        ScheduleEventType eventType = new ScheduleEventType("TestEvent", 1);
        eventType.getCreators().add(roleService.getRoleByName("ROLE_ADMIN"));
        eventType = this.eventTypeRepository.save(eventType);
    }
}
