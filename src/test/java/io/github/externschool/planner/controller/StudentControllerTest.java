package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.CourseDTO;
import io.github.externschool.planner.dto.ParticipantDTO;
import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.emailservice.EmailService;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.service.CourseService;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.StudentService;
import io.github.externschool.planner.service.StudyPlanService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.FAKE_MAIL_DOMAIN;
import static io.github.externschool.planner.util.Constants.UK_COURSE_ADMIN_IN_CHARGE;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_EVENT_TYPE_TEST;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_SELECTING_TEST_WORKS;
import static io.github.externschool.planner.util.Constants.UK_UNSUBSCRIBE_SCHEDULE_EVENT_USER_NOT_FOUND_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private StudentService studentService;
    @Autowired private PersonService personService;
    @Autowired private UserService userService;
    @Autowired private VerificationKeyService keyService;
    @Autowired private ConversionService conversionService;
    @Autowired private RoleService roleService;
    @Autowired private CourseService courseService;
    @Autowired private TeacherService teacherService;
    @Autowired private StudyPlanService planService;
    @Autowired private SchoolSubjectService subjectService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private ScheduleEventTypeService typeService;
    @Autowired private UserRepository userRepository;
    @Autowired private VerificationKeyRepository keyRepository;
    @Autowired private EmailService emailService;
    private StudentController controller;

    private MockMvc mockMvc;
    private Student student;
    private User user;
    private VerificationKey key;
    private final String userName = "some@email.com";
    private final String firstName = "StudentsName";
    private MultiValueMap<String, String> map;
    private Course course;
    private StudyPlan plan;
    private Teacher teacher;
    private User userTeacher;
    private static final String nameTeacher = "teacher@email.com";

    @Before
    public void setup(){
        controller = new StudentController(
                studentService,
                personService,
                userService,
                keyService,
                conversionService,
                roleService,
                courseService,
                teacherService,
                planService,
                scheduleService,
                typeService,
                emailService);

        key = new VerificationKey();
        keyService.saveOrUpdateKey(key);

        student = new Student();
        student.setLastName("A");
        student.setFirstName(firstName);
        student.setPatronymicName("B");
        student.setGender(Gender.FEMALE);
        student.setDateOfBirth(LocalDate.of(2010, 5, 20));
        student.setGradeLevel(GradeLevel.LEVEL_3);
        student.setPhoneNumber("(000)000-0000");
        student.setAddress("Address 123");
        student.addVerificationKey(key);
        studentService.saveOrUpdateStudent(student);

        StudentDTO studentDTO = conversionService.convert(student, StudentDTO.class);
        map = new LinkedMultiValueMap<>();
        if (studentDTO != null) {
            map.add("id", studentDTO.getId().toString());
            map.add("lastName", studentDTO.getLastName());
            map.add("firstName", studentDTO.getFirstName());
            map.add("patronymicName", studentDTO.getPatronymicName());
            map.add("gender", conversionService.convert(studentDTO.getGender(), String.class));
            map.add("dateOfBirth", conversionService.convert(studentDTO.getDateOfBirth(), String.class));
            map.add("grade", String.valueOf(studentDTO.getGradeLevel()));
            map.add("phoneNumber", studentDTO.getPhoneNumber());
            map.add("address", studentDTO.getAddress());
            map.add("verificationKey", studentDTO.getVerificationKey().getValue());
        }
        user = userService.createUser(userName,"pass", "ROLE_STUDENT");
        user.addVerificationKey(key);
        userService.save(user);

        VerificationKey keyTeacher = new VerificationKey();
        keyService.saveOrUpdateKey(keyTeacher);

        SchoolSubject subject = new SchoolSubject();
        subject.setTitle("Subject");
        subjectService.saveOrUpdateSubject(subject);
        StudyPlan plan = new StudyPlan(student.getGradeLevel(), subject);
        plan.setTitle(subject.getTitle());
        planService.saveOrUpdatePlan(plan);
        Course course = new Course(student.getId(), plan.getId());
        course.setTitle(plan.getTitle());
        courseService.saveOrUpdateCourse(course);

        teacher = new Teacher();
        teacher.setLastName(UK_COURSE_NO_TEACHER);
        teacher.addVerificationKey(keyTeacher);
        teacherService.saveOrUpdateTeacher(teacher);

        userTeacher = userService.createUser(nameTeacher,"pass", "ROLE_TEACHER");
        userTeacher.addVerificationKey(keyTeacher);
        userService.save(userTeacher);

        course.setTeacher(teacher);
        courseService.saveOrUpdateCourse(course);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = nameTeacher, roles = "TEACHER")
    public void shouldReturnMAV_whenDisplayStudentListToPrincipalTeacherRole() throws Exception {
        mockMvc.perform(get("/student/"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students",
                        "level",
                        "teacherId"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Student> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(firstName)))))
                .andExpect(model().attribute("teacherId",
                        teacher.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnMAV_whenDisplayStudentListToPrincipalAdminRole() throws Exception {
        mockMvc.perform(get("/student/"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students",
                        "level"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Student> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(firstName)))))
                .andExpect(model().attribute("teacherId",
                        Matchers.nullValue()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnStudentList_whenGetStudentWithSearchWhichDoMatch() throws Exception {
        mockMvc.perform(get("/student/").param("search", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Person> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(student.getFirstName())))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnEmptyList_whenGetStudentWithSearchWhichNotMatch() throws Exception {
        mockMvc.perform(get("/student/").param("search", "RequestDoesNotMatch"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attribute("students", Matchers.empty()));
    }

    @Test
    @WithMockUser(roles = {"TEACHER","ADMIN"})
    public void shouldReturnMaV_whenGetStudentSearch() throws Exception {
        mockMvc.perform(get("/student/search/" + student.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    @WithMockUser(username = nameTeacher, roles = "TEACHER")
    public void shouldReturnMAV_whenDisplayAllStudentsListByTeacherIdTeacherRole() throws Exception {
        mockMvc.perform(get("/student/teacher/" + teacher.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students",
                        "level",
                        "teacherId"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Student> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(firstName)))))
                .andExpect(model().attribute("teacherId",
                        teacher.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnMAV_whenDisplayAllStudentsListByTeacherIdAdminRole() throws Exception {
        mockMvc.perform(get("/student/teacher/" + teacher.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students",
                        "level",
                        "teacherId"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Student> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(firstName)))))
                .andExpect(model().attribute("teacherId",
                        teacher.getId()));
    }

    @Test
    @WithMockUser(roles = {"TEACHER","ADMIN"})
    public void shouldReturnMAV_whenDisplayStudentListByTeacherIdByGrade() throws Exception {
        mockMvc.perform(get("/student/teacher/" + teacher.getId() + "/grade/" + student.getGradeLevel().getValue()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students",
                        "level",
                        "teacherId"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Student> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(firstName)))))
                .andExpect(model().attribute("level",
                        student.getGradeLevel().getValue()))
                .andExpect(model().attribute("teacherId",
                        teacher.getId()));
    }

    @Test
    @WithMockUser(roles = {"TEACHER","ADMIN"})
    public void shouldReturnStudentListTemplate_whenGetStudentListByGrade() throws Exception {
        mockMvc.perform(get("/student/grade/3"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Student> hasProperty("gradeLevel",
                                        Matchers.equalTo(3)))));
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    public void shouldReturnStudentListTemplate_whenGetStudentWithTeacherRole() throws Exception {
        VerificationKey key = new VerificationKey();
        keyService.saveOrUpdateKey(key);
        User userTeacher = userService.createUser("teacher", "hhh", "ROLE_TEACHER");
        userTeacher.addVerificationKey(key);
        teacher.addVerificationKey(key);
        teacherService.saveOrUpdateTeacher(teacher);
        userService.save(userTeacher);
        plan = new StudyPlan();
        planService.saveOrUpdatePlan(plan);
        String title = "New Plan for Course";
        course = new Course(student.getId(), plan.getId());
        course.setTitle(title);
        course.setTeacher(teacher);
        courseService.saveOrUpdateCourse(course);

        mockMvc.perform(get("/student/"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Student> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(firstName)))));
    }

    @Test
    @WithMockUser(username = "teacher2", roles = "TEACHER")
    public void shouldReturnStudentListTemplate_whenGetStudentListByGradeWithTeacherRole() throws Exception {
        VerificationKey key = new VerificationKey();
        keyService.saveOrUpdateKey(key);
        User userTeacher = userService.createUser("teacher2", "hhh", "ROLE_TEACHER");
        userTeacher.addVerificationKey(key);
        teacher.addVerificationKey(key);
        teacherService.saveOrUpdateTeacher(teacher);
        userService.save(userTeacher);
        plan = new StudyPlan();
        planService.saveOrUpdatePlan(plan);
        String title = "New Plan for Course";
        course = new Course(student.getId(), plan.getId());
        course.setTitle(title);
        course.setTeacher(teacher);
        courseService.saveOrUpdateCourse(course);

        mockMvc.perform(get("/student/grade/3"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list"))
                .andExpect(content().string(Matchers.containsString("Student List")))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("students",
                        Matchers.hasItem(
                                Matchers.<Student> hasProperty("gradeLevel",
                                        Matchers.equalTo(3)))))
                .andExpect(model().attribute("level",
                        Matchers.equalTo(3)));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    public void shouldReturnForbidden_whenGetUnauthorized() throws Exception {
        mockMvc.perform(get("/student/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldReturnOk_whenGetFormStudentProfile() throws Exception {
        mockMvc.perform(get("/student/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(content().string(Matchers.containsString("Student Profile")))
                .andExpect(model().attributeExists("isNew", "student"))
                .andExpect(model().attribute("student",
                        Matchers.hasProperty("firstName",
                                Matchers.equalToIgnoringCase(firstName))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_whenPostId() throws Exception {
        mockMvc.perform(post("/student/" + student.getId()).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(content().string(Matchers.containsString("Student Profile")))
                .andExpect(model().attributeExists("isNew", "student"))
                .andExpect(model().attribute("student",
                        Matchers.hasProperty("firstName",
                                Matchers.equalToIgnoringCase(firstName))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnMaV_whenDisplayStudentListFormDeleteModal() throws Exception {
        Long id = student.getId();

        mockMvc.perform(get("/student/{id}/delete-modal", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_list :: deleteStudent"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirect_whenPostDelete() throws Exception {
        mockMvc.perform(post("/student/{id}/delete", + student.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/"));
    }


    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldDeleteUser_WhenRequestDeleteInvalidEmail() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        Student student = new Student();
        VerificationKey key = keyService.saveOrUpdateKey(new VerificationKey());
        student.addVerificationKey(key);
        studentService.saveOrUpdateStudent(student);
        List<Student> students = studentService.findAllStudents();
        int sizeBefore = students.size();
        User user = userService.createUser("fake@" + FAKE_MAIL_DOMAIN, "pass", "ROLE_STUDENT");
        user.addVerificationKey(key);
        userService.save(user);
        Long id = Optional.ofNullable(student.getId()).orElse(0L);
        String email = Optional.ofNullable(studentService.findStudentById(id))
                .map(Student::getVerificationKey)
                .map(VerificationKey::getUser)
                .map(User::getEmail)
                .orElse(null);

        assertThat(emailService.emailIsValid(email))
                .isEqualTo(false);

        mockMvc.perform(post("/student/{id}/delete", id).with(csrf()));

        assertThat(studentService.findAllStudents().size()).isEqualTo(sizeBefore - 1);

        assertThat(userService.getUserByEmail(email))
                .isNull();
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldSetGuestRoleToUser_WhenRequestDeleteValidEmail() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        Student student = new Student();
        VerificationKey key = keyService.saveOrUpdateKey(new VerificationKey());
        student.addVerificationKey(key);
        studentService.saveOrUpdateStudent(student);
        List<Student> students = studentService.findAllStudents();
        int sizeBefore = students.size();
        User user = userService.createUser("valid@mail", "pass", "ROLE_STUDENT");
        user.addVerificationKey(key);
        userService.save(user);
        Long id = Optional.ofNullable(student.getId()).orElse(0L);
        String email = Optional.ofNullable(studentService.findStudentById(id))
                .map(Student::getVerificationKey)
                .map(VerificationKey::getUser)
                .map(User::getEmail)
                .orElse(null);

        assertThat(emailService.emailIsValid(email))
                .isEqualTo(true);

        mockMvc.perform(post("/student/{id}/delete", id).with(csrf()));

        assertThat(studentService.findAllStudents())
                .hasSize(sizeBefore - 1);

        assertThat(userService.getUserByEmail(email))
                .isNotNull()
                .hasFieldOrPropertyWithValue("roles",
                        new HashSet<Role>(Collections.singletonList(roleService.getRoleByName("ROLE_GUEST"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirect_whenPostAdd() throws Exception {
        mockMvc.perform(post("/student/add").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnModelAndView_whenGetPlanWithExistingId() throws Exception {
        mockMvc.perform(get("/student/" + student.getId() + "/plan"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_plan"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirect_whenGetPlanWithWrongId() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(get("/student/0/plan"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnModelAndView_whenShowStudentPlanForm() throws Exception {
        String studentData = student.getLastName() + " " +
                student.getFirstName() + " " +
                student.getPatronymicName() + ", " +
                student.getGradeLevel().toString();

        mockMvc.perform(get("/student/" + student.getId() + "/plan"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_plan"))
                .andExpect(model().attribute("studentData", studentData))
                .andExpect(model().attribute("studentId", student.getId()))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("course", new CourseDTO(0L, 0L)))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attribute("coursePlanId", 0L));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnModelAndView_whenShowStudentPlanFormToEditTeacher() throws Exception {
        String studentData = student.getLastName() + " " +
                student.getFirstName() + " " +
                student.getPatronymicName() + ", " +
                student.getGradeLevel().toString();
        plan = new StudyPlan();
        planService.saveOrUpdatePlan(plan);
        String title = "New Plan for Course";
        course = new Course(student.getId(), plan.getId());
        course.setTitle(title);
        courseService.saveOrUpdateCourse(course);

        mockMvc.perform(get("/student/" + course.getStudentId() + "/plan/" + course.getPlanId()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_plan"))
                .andExpect(model().attribute("studentData", studentData))
                .andExpect(model().attribute("studentId", student.getId()))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("course",
                        Matchers.hasProperty("title", Matchers.equalTo(title))))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attribute("coursePlanId", course.getPlanId()));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnModelAndView_whenProcessStudentPlanFormActionTeacher() throws Exception {
        String studentData = student.getLastName() + " " +
                student.getFirstName() + " " +
                student.getPatronymicName() + ", " +
                student.getGradeLevel().toString();
        plan = new StudyPlan();
        planService.saveOrUpdatePlan(plan);
        String title = "New Plan for Course";
        course = new Course(student.getId(), plan.getId());
        course.setTitle(title);
        courseService.saveOrUpdateCourse(course);

        mockMvc.perform(post("/student/" + course.getStudentId() + "/plan/" + course.getPlanId())
                .param("action", "teacher").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_plan"))
                .andExpect(model().attribute("studentData", studentData))
                .andExpect(model().attribute("studentId", student.getId()))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("course", new CourseDTO(0L, 0L)))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attribute("coursePlanId", 0L));
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldRedirect_whenPostUpdateActionSaveStudent() throws Exception {
        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map).with(csrf()))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/" + student.getId() + "/plan"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirect_whenPostUpdateActionSaveAdmin() throws Exception {
        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map).with(csrf()))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/" + student.getId() + "/plan"));
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldNotAddNewKeyAndNewUser_whenStudentPostUpdateSave() throws Exception {
        map = new LinkedMultiValueMap<>();
        map.add("id", student.getId().toString());
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");
        map.add("grade", GradeLevel.LEVEL_8.toString());
        map.add("verificationKey", key.getValue());

        long userNumber = userRepository.count();
        long keyNumber = keyRepository.count();

        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map));

        assertThat(userRepository.count())
                .isEqualTo(userNumber);

        assertThat(keyRepository.count())
                .isEqualTo(keyNumber);

        assertThat(keyRepository.findAll())
                .contains(key);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldNotAddNewKeyAndNewUser_whenAdminPostUpdateSaveExistingStudent() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        map = new LinkedMultiValueMap<>();
        map.add("id", student.getId().toString());
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");
        map.add("grade", GradeLevel.LEVEL_8.toString());
        map.add("verificationKey", key.getValue());

        long userNumber = userRepository.count();
        long keyNumber = keyRepository.count();

        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map));

        assertThat(userRepository.count())
                .isEqualTo(userNumber);

        assertThat(keyRepository.count())
                .isEqualTo(keyNumber);

        assertThat(keyRepository.findAll())
                .contains(key);
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldAddNewKeyAndNewUser_whenAdminPostUpdateSaveNewProfile() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        map = new LinkedMultiValueMap<>();
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");
        map.add("grade", GradeLevel.LEVEL_8.toString());

        long userNumber = userRepository.count();
        long keyNumber = keyRepository.count();

        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map).with(csrf()));

        assertThat(userRepository.count())
                .isEqualTo(userNumber + 1);

        assertThat(keyRepository.count())
                .isEqualTo(keyNumber + 1);
    }


    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldReturnFormBack_whenPostUpdateActionSaveInvalidDate() throws Exception {
        map.remove("dateOfBirth");
        map.add("dateOfBirth", "123");

        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(model().attribute("error", UK_FORM_VALIDATION_ERROR_MESSAGE));
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldReturnFormBack_whenPostUpdateActionSaveEmptyField() throws Exception {
        map.remove("firstName");
        map.add("firstName", "");
        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(model().attribute("error", UK_FORM_VALIDATION_ERROR_MESSAGE));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirect_whenGetUpdateCancelAdmin() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(get("/student/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldRedirect_whenGetUpdateCancelStudent() throws Exception {
        mockMvc.perform(get("/student/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldUnbindOldUserFromProfile_whenPostUpdateActionNewKey() throws Exception {
        mockMvc.perform(post("/student/" + student.getId() + "/new-key").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(model().attribute("student",
                        Matchers.hasProperty("verificationKey",
                                Matchers.hasProperty("user",
                                        Matchers.not(user)))));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldSetNewKeyToDTO_whenPostUpdateActionNewKey() throws Exception {
        mockMvc.perform(post("/student/" + student.getId() + "/new-key").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(model().attribute("student",
                        Matchers.hasProperty("verificationKey",
                                Matchers.not(key))));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldKeepSameNumberOfKeysAndUsers_whenPostUpdateActionNewKey() throws Exception {
        long userNumber = userRepository.count();
        long keyNumber = keyRepository.count();

        mockMvc.perform(post("/student/" + student.getId() + "/new-key").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(model().attribute("student",
                        Matchers.hasProperty("verificationKey",
                                Matchers.not(key))));

        assertThat(userRepository.count())
                .isEqualTo(userNumber);

        assertThat(keyRepository.count())
                .isEqualTo(keyNumber);
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldReturnMAV_whenDisplaySubscriptionsToStudent() throws Exception {
        mockMvc.perform(get("/student/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_subscriptions"))
                .andExpect(model()
                        .attributeExists(
                                "student",
                                "participants"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnMAV_whenDisplaySubscriptionsToAdmin() throws Exception {
        mockMvc.perform(get("/student/" + student.getId() + "/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_subscriptions"))
                .andExpect(model()
                        .attributeExists(
                                "student",
                                "participants"));
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldReturnMAV_whenDisplayTeachersListToStudent() throws Exception {
        mockMvc.perform(get("/student/teacher/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_schedule"))
                .andExpect(model()
                        .attributeExists("student",
                                "teacher",
                                "teachers",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attributeDoesNotExist("recentUpdate"))
                .andExpect(model()
                        .attribute("availableEvents", 0L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnMAV_whenDisplayTeachersListToAdmin() throws Exception {
        userTeacher = userService.createUser("teacher2@mail.co", "pass", "ROLE_TEACHER");
        userService.createNewKeyWithNewPersonAndAddToUser(userTeacher);
        userService.assignNewRole(userTeacher, "ROLE_TEACHER");
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher(
                userTeacher.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        ScheduleEventType type = ScheduleEventTypeFactory.createScheduleEventType();
        userTeacher.getRoles().forEach(type::addOwner);
        user.getRoles().forEach(type::addParticipant);
        typeService.saveEventType(type);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(scheduleService.getCurrentWeekFirstDay())
                .withEventType(type.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(userTeacher, eventDTO, 30);

        scheduleService.removeOwner(event);
        scheduleService.addOwner(userTeacher, event);
        assertThat(event.getModifiedAt())
                .isNotNull();

        mockMvc.perform(get("/student/" + student.getId() + "/teacher/" + teacher.getId() + "/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_schedule"))
                .andExpect(model()
                        .attributeExists(
                                "teacher",
                                "teachers",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attribute("recentUpdate", Matchers.notNullValue()))
                .andExpect(model()
                        .attributeExists("availableEvents"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT", "ADMIN"})
    public void shouldReturnMAV_whenDisplayTeacherSchedule() throws Exception {
        mockMvc.perform(get("/student/" + student.getId() + "/teacher/" + student.getId() + "/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_schedule"))
                .andExpect(model()
                        .attributeExists(
                                "teacher",
                                "teachers",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attributeDoesNotExist("recentUpdate"))
                .andExpect(model()
                        .attribute("availableEvents", 0L));
    }

    @Test
    @WithMockUser(roles = {"STUDENT", "ADMIN"})
    public void shouldReturnMAV_whenDisplaySubscriptionModal() throws Exception {
        userTeacher = userService.createUser("teacher2@mail.co", "pass", "ROLE_TEACHER");
        userService.createNewKeyWithNewPersonAndAddToUser(userTeacher);
        userService.assignNewRole(userTeacher, "ROLE_TEACHER");
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher(
                userTeacher.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        ScheduleEventType type = ScheduleEventTypeFactory.createScheduleEventType();
        userTeacher.getRoles().forEach(type::addOwner);
        userService.save(userTeacher);
        user.getRoles().forEach(type::addParticipant);
        typeService.saveEventType(type);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(scheduleService.getCurrentWeekFirstDay())
                .withEventType(type.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(userTeacher, eventDTO, 30);
        scheduleService.removeOwner(event);
        scheduleService.addOwner(userTeacher, event);

        mockMvc.perform(get("/student/" + student.getId()
                + "/teacher/" + teacher.getId() + "/event/" + event.getId() + "/subscribe"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_schedule :: subscribeEvent"))
                .andExpect(model()
                        .attributeExists(
                                "teacher",
                                "teachers",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attribute("recentUpdate", Matchers.notNullValue()))
                .andExpect(model()
                        .attributeExists("availableEvents"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnMAV_whenProcessSubscriptionModal() throws Exception {
        userTeacher = userService.createUser("teacher2@mail.co", "pass", "ROLE_TEACHER");
        userService.createNewKeyWithNewPersonAndAddToUser(userTeacher);
        userService.assignNewRole(userTeacher, "ROLE_TEACHER");
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher(
                userTeacher.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));
        teacher.setLastName("Teacher");

        ScheduleEventType type = ScheduleEventTypeFactory.createScheduleEventType();
        userTeacher.getRoles().forEach(type::addOwner);
        user.getRoles().forEach(type::addParticipant);
        typeService.saveEventType(type);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(LocalDate.now())
                .withEventType(type.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(userTeacher, eventDTO, 30);

        mockMvc.perform(post("/student/" + student.getId()
                + "/teacher/" + teacher.getId() + "/event/" + event.getId() + "/subscribe").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/" + student.getId()
                        + "/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = userName, roles = {"STUDENT"})
    public void shouldReturnMAV_whenUnsuccessfulProcessSubscriptionModal() throws Exception {
        User eventUser = userService.createUser("teacher2@mail.co", "pass", "ROLE_TEACHER");
        userService.createNewKeyWithNewPersonAndAddToUser(eventUser);
        userService.assignNewRole(eventUser, "ROLE_TEACHER");
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher(
                eventUser.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));
        teacher.setLastName("Teacher");

        ScheduleEventType wrongType = ScheduleEventTypeFactory.createScheduleEventType();
        eventUser.getRoles().forEach(wrongType::addOwner);
        typeService.saveEventType(wrongType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(LocalDate.now())
                .withEventType(wrongType.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();

        ScheduleEvent wrongEvent = scheduleService.createEventWithDuration(eventUser, eventDTO, 30);

        mockMvc.perform(post("/student/" + student.getId() + "/teacher/"
                + teacher.getId() + "/event/" + wrongEvent.getId() + "/subscribe").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_schedule"))
                .andExpect(model()
                        .attributeExists("error"))
                .andExpect(model()
                        .attribute("error", Matchers.notNullValue()));
    }

    @Test
    @WithMockUser(username = "anyone@x", roles = {"STUDENT"})
    public void shouldReturnMAVError_whenUnsuccessfulProcessSubscriptionForAdminInChargeEvent() throws Exception {
        Student studentPerson =   new Student(new Person(),
                LocalDate.now().minusYears(10),
                Gender.FEMALE,
                "",
                GradeLevel.LEVEL_5);
        User studentUser = userService.createUser("anyone@x", "q", "ROLE_STUDENT");
        studentUser = userService.save(studentUser);
        VerificationKey verificationKey = keyService.saveOrUpdateKey(new VerificationKey());
        studentPerson.addVerificationKey(verificationKey);
        studentService.saveOrUpdateStudent(studentPerson);
        studentUser.addVerificationKey(verificationKey);
        studentUser.setEnabled(true);
        userService.save(studentUser);

        User eventOwner = userService.createUser("teacher2@x", "pass", "ROLE_ADMIN");
        userService.createNewKeyWithNewPersonAndAddToUser(eventOwner);
        userService.assignNewRole(eventOwner, "ROLE_ADMIN");
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher(
                eventOwner.getVerificationKey().getPerson(),
                UK_COURSE_ADMIN_IN_CHARGE,
                new HashSet<>(),
                new HashSet<>()));
        teacher.setLastName(UK_COURSE_ADMIN_IN_CHARGE);

        SchoolSubject subject = new SchoolSubject();
        subject.setTitle("Subject");
        subjectService.saveOrUpdateSubject(subject);
        StudyPlan plan = new StudyPlan(studentPerson.getGradeLevel(), subject);
        plan.setTitle(subject.getTitle());
        planService.saveOrUpdatePlan(plan);
        Course course = new Course(studentPerson.getId(), plan.getId());
        course.setTitle(plan.getTitle());
        course.setTeacher(teacher);
        courseService.saveOrUpdateCourse(course);

        ScheduleEventType type = ScheduleEventTypeFactory.createScheduleEventType();
        eventOwner.getRoles().forEach(type::addOwner);
        studentUser.getRoles().forEach(type::addParticipant);
        typeService.saveEventType(type);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle(UK_EVENT_TYPE_TEST)
                .withDate(LocalDate.now())
                .withEventType(type.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(eventOwner, eventDTO, 30);

        ParticipantDTO participantDTO = scheduleService.addParticipant(studentUser, event)
                .map(participant -> scheduleService.saveParticipant(participant))
                .map(participant -> conversionService.convert(participant, ParticipantDTO.class))
                .orElse(new ParticipantDTO(100500L));
        participantDTO.setPlanOneTitle(plan.getTitle());
        participantDTO.setPlanOneId(plan.getId());
        participantDTO.setPlanOneSemesterOne(true);

        mockMvc.perform(post("/student/" + studentPerson.getId() + "/teacher/"
                + teacher.getId() + "/event/" + event.getId() + "/subscribe")
                .requestAttr("participant", participantDTO).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_schedule"))
                .andExpect(model()
                        .attributeExists("error"))
                .andExpect(model()
                        .attribute("error", UK_FORM_VALIDATION_ERROR_SELECTING_TEST_WORKS));

        scheduleService.deleteEventById(event.getId());
    }

    @Test
    @WithMockUser(roles = {"STUDENT", "ADMIN"})
    public void shouldReturnMAV_whenDisplayUnsubscribeModal() throws Exception {
        userTeacher = userService.createUser("teacher2@mail.co", "pass", "ROLE_TEACHER");
        userService.createNewKeyWithNewPersonAndAddToUser(userTeacher);
        userService.assignNewRole(userTeacher, "ROLE_TEACHER");
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher(
                userTeacher.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        ScheduleEventType type = ScheduleEventTypeFactory.createScheduleEventType();
        userTeacher.getRoles().forEach(type::addOwner);
        user.getRoles().forEach(type::addParticipant);
        typeService.saveEventType(type);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(scheduleService.getCurrentWeekFirstDay())
                .withEventType(type.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(userTeacher, eventDTO, 30);
        scheduleService.removeOwner(event);
        scheduleService.addOwner(userTeacher, event);

        mockMvc.perform(get("/student/" + student.getId()
                + "/teacher/" + teacher.getId() + "/event/" + event.getId() + "/unsubscribe"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_schedule :: unsubscribe"))
                .andExpect(model()
                        .attributeExists(
                                "teacher",
                                "teachers",
                                "weekDays",
                                "currentWeek",
                                "nextWeek",
                                "currentWeekEvents",
                                "nextWeekEvents",
                                "availableEvents",
                                "event"))
                .andExpect(model()
                        .attribute("recentUpdate", Matchers.notNullValue()))
                .andExpect(model()
                        .attributeExists("availableEvents"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT", "ADMIN"})
    public void shouldReturnMAV_whenSuccessfulProcessUnsubscribeModal() throws Exception {
        userTeacher = userService.createUser("teacher2@mail.co", "pass", "ROLE_TEACHER");
        userService.createNewKeyWithNewPersonAndAddToUser(userTeacher);
        userService.assignNewRole(userTeacher, "ROLE_TEACHER");
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher(
                userTeacher.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        ScheduleEventType type = ScheduleEventTypeFactory.createScheduleEventType();
        userTeacher.getRoles().forEach(type::addOwner);
        user.getRoles().forEach(type::addParticipant);
        typeService.saveEventType(type);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(LocalDate.now())
                .withEventType(type.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(userTeacher, eventDTO, 30);
        scheduleService.addParticipant(user, event);

        mockMvc.perform(post("/student/" + student.getId()
                + "/teacher/" + teacher.getId() + "/event/" + event.getId() + "/unsubscribe").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/" + student.getId()
                        + "/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT", "ADMIN"})
    public void shouldReturnMAV_whenUnsuccessfulProcessUnsubscribeModal() throws Exception {
        userTeacher = userService.createUser("teacher2@mail.co", "pass", "ROLE_TEACHER");
        userService.createNewKeyWithNewPersonAndAddToUser(userTeacher);
        userService.assignNewRole(userTeacher, "ROLE_TEACHER");
        Teacher teacher = teacherService.saveOrUpdateTeacher(new Teacher(
                userTeacher.getVerificationKey().getPerson(),
                "Official",
                new HashSet<>(),
                new HashSet<>()));

        ScheduleEventType wrongType = ScheduleEventTypeFactory.createScheduleEventType();
        userTeacher.getRoles().forEach(wrongType::addOwner);
        typeService.saveEventType(wrongType);

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle("Test")
                .withDate(LocalDate.now())
                .withEventType(wrongType.getName())
                .withStartTime(LocalTime.MAX)
                .withIsOpen(true)
                .build();
        ScheduleEvent wrongEvent = scheduleService.createEventWithDuration(userTeacher, eventDTO, 30);

        mockMvc.perform(post("/student/" + student.getId() + "/teacher/"
                + teacher.getId() + "/event/" + wrongEvent.getId() + "/unsubscribe").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_schedule"))
                .andExpect(model()
                        .attributeExists("error"))
                .andExpect(model()
                        .attribute("error", UK_UNSUBSCRIBE_SCHEDULE_EVENT_USER_NOT_FOUND_ERROR_MESSAGE));
    }

    @After
    public void tearDown() {
        studentService.deleteStudentById(student.getId());
        userService.deleteUser(user);
        userService.deleteUser(userTeacher);
        keyService.deleteById(key.getId());
        Optional.ofNullable(plan).ifPresent(p -> planService.deletePlan(p));
        teacherService.deleteTeacherById(teacher.getId());
    }
}
