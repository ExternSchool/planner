package io.github.externschool.planner.bootstrapdata;

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
import io.github.externschool.planner.repository.CourseRepository;
import io.github.externschool.planner.repository.StudyPlanRepository;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.StudentService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    @Autowired private StudyPlanRepository planRepository;
    @Autowired private StudentService studentService;
    @Autowired private CourseRepository courseRepository;
    @Autowired private PersonService personService;

    private final RoleService roleService;

    public BootstrapDataPopulator(final UserService userService,
                                  final TeacherService teacherService,
                                  final SchoolSubjectService schoolSubjectService,
                                  final ScheduleEventTypeRepository eventTypeRepository,
                                  final VerificationKeyService verificationKeyService,
                                  final RoleService roleService) {
        this.userService = userService;
        this.teacherService = teacherService;
        this.schoolSubjectService = schoolSubjectService;
        this.verificationKeyService = verificationKeyService;
        this.eventTypeRepository = eventTypeRepository;
        this.roleService = roleService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createScheduleEventType();

        User admin = userService.createUser("q@q", "q", "ROLE_ADMIN");
        userService.saveOrUpdate(admin);
        Person adminPerson = new Person();
        adminPerson.setLastName("Admin");
        adminPerson.setFirstName("Admin");
        adminPerson.setPatronymicName("Admin");
        adminPerson.setPhoneNumber("(099)999-9999");
        personService.saveOrUpdatePerson(adminPerson);
        VerificationKey key = new VerificationKey();
        verificationKeyService.saveOrUpdateKey(key);
        adminPerson.addVerificationKey(key);
        admin.addVerificationKey(key);
        personService.saveOrUpdatePerson(adminPerson);
        userService.saveOrUpdate(admin);

        key = new VerificationKey();
        verificationKeyService.saveOrUpdateKey(key);
        Teacher teacher = createTeacher(new Person(
                        null,
                        "James",
                        "Merrill",
                        "Carlsmith",
                        "(066)666-6666"),
                key,
                "Psychologist",
                Collections.singletonList("Cognitive dissonance theory"));
        teacherService.saveOrUpdateTeacher(teacher);

        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        teacher = createTeacher(new Person(
                        null,
                        "Alexander",
                        "Sutherland",
                        "Neill",
                        "(099)999-9999"),
                key,
                "Principal",
                Arrays.asList("Quantum Mechanics", "Algebraic topology"));
        teacherService.saveOrUpdateTeacher(teacher);

        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        teacher = createTeacher(new Person(
                        null,
                        "Leonardo",
                        "di ser Piero",
                        "da Vinci",
                        "(099)999-1111"),
                key,
                "Teacher",
                Arrays.asList("Anatomy and physiology", "Rocket Science"));
        teacherService.saveOrUpdateTeacher(teacher);

        key = verificationKeyService.saveOrUpdateKey(new VerificationKey());
        Student student = new Student(new Person(77L,
                "Jonh",
                "Johnovich",
                "Doe",
                "(044)222-2222"),
                LocalDate.of(2006, 02, 22),
                Gender.MALE,
                "Khreshchatyk str., 11, ap.11, Kyiv 01001",
                GradeLevel.LEVEL_7);
        student.addVerificationKey(key);
        studentService.saveOrUpdateStudent(student);

        List<StudyPlan> plans = planRepository.findAllByGradeLevelOrderBySubject(student.getGradeLevel());
        for (StudyPlan plan : plans) {
            Course course = new Course(student.getId(), plan.getId());
            courseRepository.save(course);
            SchoolSubject subj = plan.getSubject();
            Teacher t = teacherService.findAllBySubject(subj).get(0);
            t.addCourse(course);
            courseRepository.save(course);
        }
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
            subject.setName(subjectName);
            schoolSubjectService.saveOrUpdateSubject(subject);

            StudyPlan plan = new StudyPlan(GradeLevel.LEVEL_7, subject);
            plan.setHoursPerSemesterOne(2);
            plan.setHoursPerSemesterTwo(2);
            plan.setExamSemesterOne(true);
            plan.setExamSemesterTwo(true);
            plan.setName("Introduction to " + plan.getName());
            planRepository.save(plan);

            teacher.addSubject(subject);
            subject.addPlan(plan);
        }

        return teacher;
    }
    
    private void createScheduleEventType() {
        ScheduleEventType eventType = new ScheduleEventType("TestEvent", 1);
        eventType.getCreators().add(roleService.getRoleByName("ROLE_ADMIN"));
        eventType = this.eventTypeRepository.save(eventType);
    }
}
