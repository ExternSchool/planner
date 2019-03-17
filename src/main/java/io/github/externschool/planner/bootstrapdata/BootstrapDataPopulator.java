package io.github.externschool.planner.bootstrapdata;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.StudentService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static io.github.externschool.planner.util.Constants.DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE;
import static io.github.externschool.planner.util.Constants.SCHOOL_PHONE_NUMBER;
import static io.github.externschool.planner.util.Constants.UK_COURSE_ADMIN_IN_CHARGE;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_CONTROL;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_CONSULT;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_OFFICIAL;

@Service
@Transactional
@ExcludeFromTests
public class BootstrapDataPopulator implements InitializingBean {
    private final UserService userService;
    private final TeacherService teacherService;
    private final SchoolSubjectService schoolSubjectService;
    private final ScheduleEventTypeRepository eventTypeRepository;
    private final VerificationKeyService verificationKeyService;
    private final StudyPlanRepository planRepository;
    private final StudentService studentService;
    private final RoleService roleService;

    //TODO REMOVE WHEN MIGRATED
    @Value("${app.username}") private String adminUsername;
    @Value("${app.password}") private String adminPassword;

    @Autowired
    public BootstrapDataPopulator(final UserService userService,
                                  final TeacherService teacherService,
                                  final SchoolSubjectService schoolSubjectService,
                                  final ScheduleEventTypeRepository eventTypeRepository,
                                  final VerificationKeyService verificationKeyService,
                                  final RoleService roleService,
                                  final StudyPlanRepository planRepository,
                                  final StudentService studentService) {
        this.userService = userService;
        this.teacherService = teacherService;
        this.schoolSubjectService = schoolSubjectService;
        this.verificationKeyService = verificationKeyService;
        this.eventTypeRepository = eventTypeRepository;
        this.roleService = roleService;
        this.planRepository = planRepository;
        this.studentService = studentService;
    }

    @Override
    public void afterPropertiesSet() {
        createPresetScheduleEventTypes();

        if (teacherService.findAllByLastName(UK_COURSE_NO_TEACHER).isEmpty()) {
            Teacher noTeacher = new Teacher();
            noTeacher.setLastName(UK_COURSE_NO_TEACHER);
            noTeacher.setFirstName("");
            noTeacher.setPatronymicName("");
            noTeacher.setOfficial("");
            noTeacher.setPhoneNumber(SCHOOL_PHONE_NUMBER);
            VerificationKey keyNoTeacher = new VerificationKey();
            verificationKeyService.saveOrUpdateKey(keyNoTeacher);
            noTeacher.addVerificationKey(keyNoTeacher);
            teacherService.saveOrUpdateTeacher(noTeacher);
        }

        if (userService.getUserByEmail(adminUsername) == null) {
            User inCharge = userService.createUser(adminUsername, adminPassword, "ROLE_ADMIN");
            inCharge = userService.save(inCharge);
            VerificationKey key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
            Teacher inChargeTeacher = createTeacher(
                    new Person(null,
                            "",
                            "",
                            UK_COURSE_ADMIN_IN_CHARGE,
                            SCHOOL_PHONE_NUMBER),
                    key,
                    "",
                    Collections.singletonList(UK_EVENT_TYPE_CONTROL));
            inChargeTeacher.addVerificationKey(key);
            inCharge.addVerificationKey(key);
            inCharge.setEnabled(true);
            teacherService.saveOrUpdateTeacher(inChargeTeacher);
            userService.save(inCharge);
        }

        //TODO Remove after been tested
        String test1Email = "mailto.benkoff@gmail.com";
        String test1Pass = "q";
        String test2Email = "bigopendatanet@gmail.com";
        String test2Pass = "q";

        //TODO Remove after been tested
        if (userService.getUserByEmail(test1Email) == null) {
            User testTeacher = userService.createUser(test1Email, test1Pass, "ROLE_TEACHER");
            testTeacher = userService.save(testTeacher);
            VerificationKey verificationKey = verificationKeyService.saveOrUpdateKey(new VerificationKey());
            Teacher testTeacherTeacher = createTeacher(
                    new Person(null,
                            "Тест",
                            "Тестович",
                            "Вчитель",
                            "000-0000"),
                    verificationKey,
                    "Якась посада є",
                    Collections.emptyList());
            testTeacherTeacher.addVerificationKey(verificationKey);
            testTeacher.addVerificationKey(verificationKey);
            testTeacher.setEnabled(true);
            teacherService.saveOrUpdateTeacher(testTeacherTeacher);
            userService.assignNewRole(testTeacher, "ROLE_OFFICER");
            userService.save(testTeacher);
        }

        //TODO Remove after been tested
        if (userService.getUserByEmail(test2Email) == null) {
            User testStudent = userService.createUser(test2Email, test2Pass, "ROLE_STUDENT");
            testStudent = userService.save(testStudent);
            VerificationKey verificationKey = verificationKeyService.saveOrUpdateKey(new VerificationKey());
            Student testStudentStudent = new Student(
                    new Person(null,
                            "Студент",
                            "Гостьович",
                            "Тестовий",
                            "000-0000"),
                    null,
                    null,
                    null,
                    GradeLevel.LEVEL_11);
            testStudentStudent.addVerificationKey(verificationKey);
            testStudent.addVerificationKey(verificationKey);
            testStudent.setEnabled(true);
            studentService.saveOrUpdateStudent(testStudentStudent);
            userService.save(testStudent);
        }
    }

    private Teacher createTeacher(Person person, VerificationKey key, String officialName, List<String> subjectsNames) {
        Teacher teacher = new Teacher(
                person.getId(),
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName(),
                person.getPhoneNumber(),
                key,
                officialName,
                new HashSet<>(),
                new HashSet<>());
        teacherService.saveOrUpdateTeacher(teacher);

        for (String subjectName : subjectsNames) {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(subjectName);
            schoolSubjectService.saveOrUpdateSubject(subject);

            Arrays.asList(
                    GradeLevel.LEVEL_5,
                    GradeLevel.LEVEL_6,
                    GradeLevel.LEVEL_7,
                    GradeLevel.LEVEL_8,
                    GradeLevel.LEVEL_9,
                    GradeLevel.LEVEL_10,
                    GradeLevel.LEVEL_11)
                    .forEach(gradeLevel -> {
                        StudyPlan plan = new StudyPlan(gradeLevel,
                                subject,
                                subject.getTitle(),
                                0,
                                0,
                                0,
                                0);
                        planRepository.save(plan);
                        teacher.addSubject(subject);
                        plan.setSubject(subject);
                    });
        }

        return teacher;
    }
    
    private void createPresetScheduleEventTypes() {
        ScheduleEventType eventType;
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_CONTROL) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_CONTROL, 30);
            eventType.addOwner(roleService.getRoleByName("ROLE_ADMIN"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_STUDENT"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE * 4);
            eventTypeRepository.save(eventType);
        }
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_CONSULT) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_CONSULT, 1);
            eventType.addOwner(roleService.getRoleByName("ROLE_TEACHER"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_STUDENT"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE);
            eventTypeRepository.save(eventType);
        }
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_OFFICIAL) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_OFFICIAL, 1);
            eventType.addOwner(roleService.getRoleByName("ROLE_OFFICER"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_GUEST"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE / 3 * 2);
            eventTypeRepository.save(eventType);
        }
    }
}
