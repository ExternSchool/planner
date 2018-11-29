package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.CourseDTO;
import io.github.externschool.planner.dto.StudentDTO;
import io.github.externschool.planner.entity.GradeLevel;
import io.github.externschool.planner.entity.StudyPlan;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Gender;
import io.github.externschool.planner.entity.profile.Student;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.CourseService;
import io.github.externschool.planner.service.PersonService;
import io.github.externschool.planner.service.RoleService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static io.github.externschool.planner.util.Constants.UK_FORM_VALIDATION_ERROR_MESSAGE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
                planService);

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

        teacher = new Teacher();
        teacher.setLastName(UK_COURSE_NO_TEACHER);
        teacherService.saveOrUpdateTeacher(teacher);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnStudentListTemplate_whenGetStudentWithAdminRole() throws Exception {
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
    @WithMockUser(roles = "ADMIN")
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
                                        Matchers.equalTo(3)))));
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
        mockMvc.perform(post("/student/" + student.getId()))
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
    public void shouldRedirect_whenPostDelete() throws Exception {
        mockMvc.perform(post("/student/" + student.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirect_whenPostAdd() throws Exception {
        mockMvc.perform(post("/student/add"))
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
                .param("action", "teacher"))
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
                .params(map))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirect_whenPostUpdateActionSaveAdmin() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldReturnFormBack_whenPostUpdateActionSaveInvalidDate() throws Exception {
        map.remove("dateOfBirth");
        map.add("dateOfBirth", "123");

        mockMvc.perform(post("/student/update")
                .param("action", "save")
                .params(map))
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
                .params(map))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(model().attribute("error", UK_FORM_VALIDATION_ERROR_MESSAGE));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirect_whenGetUpdateCancelAdmin() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(get("/student/cancel/" + student.getVerificationKey().getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "STUDENT")
    public void shouldRedirect_whenGetUpdateCancelStudent() throws Exception {
        mockMvc.perform(get("/student/cancel/" + student.getVerificationKey().getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldUnbindOldUserFromProfile_whenPostUpdateActionNewKey() throws Exception {
        mockMvc.perform(post("/student/" + student.getId() + "/new-key"))
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
        mockMvc.perform(post("/student/" + student.getId() + "/new-key"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_profile"))
                .andExpect(model().attribute("student",
                        Matchers.hasProperty("verificationKey",
                                Matchers.not(key))));
    }

    @After
    public void tearDown() {
        studentService.deleteStudentById(student.getId());
        userService.deleteUser(user);
        keyService.deleteById(key.getId());
        Optional.ofNullable(plan).ifPresent(p -> planService.deletePlan(p));
        teacherService.deleteTeacherById(teacher.getId());
    }
}
