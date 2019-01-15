package io.github.externschool.planner.bootstrapdata;

import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleService;
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
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_DEPUTY;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_GRADE_BOOK;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_GROUP;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_PERSONAL;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_PRINCIPAL;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_PSYCHOLOGIST;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_TEST;

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
    private final CourseRepository courseRepository;
    private final PersonService personService;
    private final RoleService roleService;
    private final ScheduleService scheduleService;
    @Value("${app.incharge.mail}") private String inchargeEmail;
    @Value("${app.incharge.pass}") private String inchargePass;

    @Autowired
    public BootstrapDataPopulator(final UserService userService,
                                  final TeacherService teacherService,
                                  final SchoolSubjectService schoolSubjectService,
                                  final ScheduleEventTypeRepository eventTypeRepository,
                                  final VerificationKeyService verificationKeyService,
                                  final RoleService roleService,
                                  final StudyPlanRepository planRepository,
                                  final StudentService studentService,
                                  final CourseRepository courseRepository,
                                  final PersonService personService,
                                  final ScheduleService scheduleService) {
        this.userService = userService;
        this.teacherService = teacherService;
        this.schoolSubjectService = schoolSubjectService;
        this.verificationKeyService = verificationKeyService;
        this.eventTypeRepository = eventTypeRepository;
        this.roleService = roleService;
        this.planRepository = planRepository;
        this.studentService = studentService;
        this.courseRepository = courseRepository;
        this.personService = personService;
        this.scheduleService = scheduleService;
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

        if (userService.getUserByEmail(inchargeEmail) == null) {
            User inCharge = userService.createUser(inchargeEmail, inchargePass, "ROLE_ADMIN");
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
                    Collections.singletonList(UK_EVENT_TYPE_TEST));
            inChargeTeacher.addVerificationKey(key);
            inCharge.addVerificationKey(key);
            inCharge.setEnabled(true);
            teacherService.saveOrUpdateTeacher(inChargeTeacher);
            userService.save(inCharge);
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
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_PERSONAL) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_PERSONAL, 1);
            eventType.addOwner(roleService.getRoleByName("ROLE_TEACHER"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_STUDENT"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE);
            eventTypeRepository.save(eventType);
        }
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_GROUP) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_GROUP, 2);
            eventType.addOwner(roleService.getRoleByName("ROLE_TEACHER"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_STUDENT"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE);
            eventTypeRepository.save(eventType);
        }
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_TEST) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_TEST, 30);
            eventType.addOwner(roleService.getRoleByName("ROLE_ADMIN"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_STUDENT"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE * 4);
            eventTypeRepository.save(eventType);
        }
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_PRINCIPAL) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_PRINCIPAL, 1);
            eventType.addOwner(roleService.getRoleByName("ROLE_OFFICER"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_GUEST"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE / 3 * 2);
            eventTypeRepository.save(eventType);
        }
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_DEPUTY) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_DEPUTY, 1);
            eventType.addOwner(roleService.getRoleByName("ROLE_OFFICER"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_GUEST"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE / 3 * 2);
            eventTypeRepository.save(eventType);
        }
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_GRADE_BOOK) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_GRADE_BOOK, 1);
            eventType.addOwner(roleService.getRoleByName("ROLE_OFFICER"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_GUEST"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE / 3);
            eventTypeRepository.save(eventType);
        }
        if (eventTypeRepository.findByName(UK_EVENT_TYPE_PSYCHOLOGIST) == null) {
            eventType = new ScheduleEventType(UK_EVENT_TYPE_PSYCHOLOGIST, 1);
            eventType.addOwner(roleService.getRoleByName("ROLE_OFFICER"));
            eventType.addParticipant(roleService.getRoleByName("ROLE_GUEST"));
            eventType.setDurationInMinutes(DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE);
            eventTypeRepository.save(eventType);
        }
    }
}
