package io.github.externschool.planner.bootstrapdata;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_GROUP;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_PERSONAL;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_PRINCIPAL;

@Service
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
                                  final PersonService personService, final ScheduleService scheduleService) {
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
        Teacher noTeacher = new Teacher();
        noTeacher.setLastName(UK_COURSE_NO_TEACHER);
        VerificationKey keyNoTeacher = new VerificationKey();
        verificationKeyService.saveOrUpdateKey(keyNoTeacher);
        noTeacher.addVerificationKey(keyNoTeacher);
        teacherService.saveOrUpdateTeacher(noTeacher);

        createScheduleEventType();

        User admin = userService.createUser("q@q", "q", "ROLE_ADMIN");
        admin = userService.saveOrUpdate(admin);
        Person adminPerson = new Person();
        adminPerson.setLastName("Admin");
        adminPerson.setFirstName("Admin");
        adminPerson.setPatronymicName("Admin");
        adminPerson.setPhoneNumber("(099)999-9999");
        personService.saveOrUpdatePerson(adminPerson);
        VerificationKey key = new VerificationKey();
        adminPerson.addVerificationKey(key);
        admin.addVerificationKey(key);
        verificationKeyService.saveOrUpdateKey(key);
        personService.saveOrUpdatePerson(adminPerson);
        userService.saveOrUpdate(admin);

        // Commented to let this teacher be Admin q@q
        User presetTeacher = userService.createUser("t@t", "t", "ROLE_TEACHER");
        presetTeacher.addRole(roleService.getRoleByName("ROLE_OFFICER"));
        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        Teacher teacher = createTeacher(new Person(
                        null,
                        "Джеймс",
                        "Меріл",
                        "Карлсміт",
                        "(066)666-6666"),
                key,
                "Психолог",
                Collections.singletonList("Теорія когнитивного дисонансу"));
        teacher.addVerificationKey(key);
        presetTeacher.addVerificationKey(key);
        teacherService.saveOrUpdateTeacher(teacher);
        userService.saveOrUpdate(presetTeacher);

        User presetStudent = userService.createUser("s@s", "s", "ROLE_STUDENT");
        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        Student presetStudentProfile = new Student(new Person(73L,
                "Гаврило",
                "Петрович",
                "Принцип",
                "(111)000-2222"),
                LocalDate.of(2006, 7, 28),
                Gender.MALE,
                "вул. Франца Фердінанда, 28, кв. 6",
                GradeLevel.LEVEL_7);
        presetStudentProfile.addVerificationKey(key);
        presetStudent.setVerificationKey(key);
        studentService.saveOrUpdateStudent(presetStudentProfile);
        userService.saveOrUpdate(presetStudent);

        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        teacher = createTeacher(new Person(
                        null,
                        "Олександр",
                        "Сазерленд",
                        "Нейл",
                        "(099)999-9999"),
                key,
                "Директор",
                Arrays.asList("Квантова механіка", "Теорія квантового поля"));
        teacherService.saveOrUpdateTeacher(teacher);

        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        teacher = createTeacher(new Person(
                        null,
                        "Леонардо",
                        "ді сер П'єро",
                        "да Вінчі",
                        "(099)999-1111"),
                key,
                "",
                Arrays.asList("Анатомія та фізіологія", "Основи ракетобудування"));
        teacherService.saveOrUpdateTeacher(teacher);

        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        Person person = new Person(null,
                "Марко",
                "Ілліч",
                "Цукерберг",
                "(044)222-2222");
        Student student = new Student(person,
                LocalDate.of(2002, 2, 22),
                Gender.MALE,
                "вул. Рекламна, 11, кв.11, Київ, 01001",
                GradeLevel.LEVEL_11);
        student.addVerificationKey(key);
        studentService.saveOrUpdateStudent(student);

        List<StudyPlan> plans = planRepository.findAllByGradeLevelOrderByTitleAsc(student.getGradeLevel());
        for (StudyPlan plan : plans) {
            Course course = new Course(student.getId(), plan.getId());
            courseRepository.save(course);
            SchoolSubject subj = plan.getSubject();
            Teacher t = teacherService.findAllBySubject(subj).get(0);
            t.addCourse(course);
            courseRepository.save(course);
        }

        Set<User> eventUsers = new HashSet<>();
        eventUsers.add(presetStudent);
        createScheduleEventsWithSetOfUsers(
                admin,
                eventUsers,
                LocalDate.now().getDayOfWeek().getValue() == 6 || LocalDate.now().getDayOfWeek().getValue() == 7
                        ? LocalDate.now().plusDays(-2L)
                        : LocalDate.now());
        createScheduleEventsWithSetOfUsers(
                admin,
                eventUsers,
                LocalDate.now().getDayOfWeek().getValue() == 6 || LocalDate.now().getDayOfWeek().getValue() == 7
                        ? LocalDate.now().plusDays(5L)
                        : LocalDate.now().plusDays(7L));
    }

    private Teacher createTeacher(Person person, VerificationKey key, String officerName, List<String> subjectsNames) {
        Teacher teacher = new Teacher(
                person.getId(),
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName(),
                person.getPhoneNumber(),
                key,
                officerName,
                new HashSet<>(),
                new HashSet<>());
        teacherService.saveOrUpdateTeacher(teacher);

        for (String subjectName : subjectsNames) {
            SchoolSubject subject = new SchoolSubject();
            subject.setTitle(subjectName);
            schoolSubjectService.saveOrUpdateSubject(subject);

            StudyPlan plan = new StudyPlan(GradeLevel.LEVEL_7, subject);
            plan.setHoursPerSemesterOne(2);
            plan.setHoursPerSemesterTwo(2);
            plan.setWorksPerSemesterOne(1);
            plan.setWorksPerSemesterTwo(1);
            plan.setTitle(plan.getTitle());
            planRepository.save(plan);

            teacher.addSubject(subject);
            subject.addPlan(plan);
        }

        return teacher;
    }
    
    private void createScheduleEventType() {
        ScheduleEventType eventType = new ScheduleEventType(UK_EVENT_TYPE_PERSONAL, 1);
        eventType.getCreators().add(roleService.getRoleByName("ROLE_ADMIN"));
        eventType.getCreators().add(roleService.getRoleByName("ROLE_TEACHER"));
        this.eventTypeRepository.save(eventType);

        eventType = new ScheduleEventType(UK_EVENT_TYPE_GROUP, 2);
        eventType.getCreators().add(roleService.getRoleByName("ROLE_ADMIN"));
        eventType.getCreators().add(roleService.getRoleByName("ROLE_TEACHER"));
        this.eventTypeRepository.save(eventType);

        eventType = new ScheduleEventType(UK_EVENT_TYPE_PRINCIPAL, 1);
        eventType.getCreators().add(roleService.getRoleByName("ROLE_ADMIN"));
        eventType.getCreators().add(roleService.getRoleByName("ROLE_OFFICER"));
        this.eventTypeRepository.save(eventType);
    }

    private void createScheduleEventsWithSetOfUsers(final User owner,
                                                    final Set<User> participants,
                                                    final LocalDate date) {

        ScheduleEventDTO eventOne =  ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(date)
                .withStartTime(LocalTime.of(9,0))
                .withEventType(UK_EVENT_TYPE_PRINCIPAL)
                .withDescription(UK_EVENT_TYPE_PRINCIPAL)
                .withTitle(owner.getVerificationKey().getPerson().getShortName())
                .withCreated(LocalDateTime.now())
                .withIsOpen(true)
                .build();
        ScheduleEventDTO eventTwo = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(date)
                .withStartTime(LocalTime.of(9,30))
                .withEventType(UK_EVENT_TYPE_GROUP)
                .withDescription(UK_EVENT_TYPE_GROUP)
                .withTitle(owner.getVerificationKey().getPerson().getShortName())
                .withCreated(LocalDateTime.now())
                .withIsOpen(true)
                .build();

        participants.forEach(user -> scheduleService.addParticipant(
                user,
                scheduleService.createEventWithDuration(owner, eventOne, 25)));
        participants.stream()
                .findAny()
                .ifPresent(user -> scheduleService.addParticipant(
                        user,
                        scheduleService.createEventWithDuration(owner, eventTwo, 15)));

        ScheduleEventDTO eventThree = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(date)
                .withStartTime(LocalTime.of(9,45))
                .withEventType(UK_EVENT_TYPE_PERSONAL)
                .withDescription(UK_EVENT_TYPE_PERSONAL)
                .withTitle(owner.getVerificationKey().getPerson().getShortName())
                .withCreated(LocalDateTime.now())
                .withIsOpen(true)
                .build();
        scheduleService.createEventWithDuration(owner, eventThree, 45);
    }
}
