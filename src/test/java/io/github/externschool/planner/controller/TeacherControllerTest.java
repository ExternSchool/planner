package io.github.externschool.planner.controller;

import io.github.externschool.planner.TestPlannerApplication;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.SchoolSubjectService;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestPlannerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TeacherControllerTest {
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private TeacherService teacherService;
    @Autowired private SchoolSubjectService subjectService;
    @Autowired private ConversionService conversionService;
    @Autowired private VerificationKeyService keyService;
    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    private TeacherController controller;
    private MockMvc mockMvc;

    private Teacher teacher;
    private Teacher noTeacher;
    private VerificationKey key;
    private VerificationKey keyNoTeacher;
    private User user;
    private final String userName = "some@email.com";

    @Before
    public void setup() {
        controller = new TeacherController(
                teacherService, subjectService, conversionService, keyService, userService, roleService);

        noTeacher = new Teacher();
        noTeacher.setLastName(UK_COURSE_NO_TEACHER);
        keyNoTeacher = new VerificationKey();
        keyService.saveOrUpdateKey(keyNoTeacher);
        noTeacher.addVerificationKey(keyNoTeacher);
        teacherService.saveOrUpdateTeacher(noTeacher);

        key = new VerificationKey();
        keyService.saveOrUpdateKey(key);
        teacher = new Teacher();
        teacher.setFirstName("First");
        teacher.setPatronymicName("Patron");
        teacher.setLastName("Last");
        teacher.setPhoneNumber("(000)000-0000");
        teacher.addVerificationKey(key);
        teacher.setOfficer("Principal");
        teacherService.saveOrUpdateTeacher(teacher);

        user = userService.createUser(userName,"pass", "ROLE_TEACHER");
        user.addVerificationKey(key);
        userService.saveOrUpdate(user);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldReturnTeacherListTemplate_WhenGetRequestRootWithAdminRole() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.saveOrUpdate(user);

        mockMvc.perform(get("/teacher/"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_list"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teachers List")));
    }

    @Test
    @WithMockUser(username = userName, roles = "TEACHER")
    public void shouldReturnTeacherProfileTemplate_WhenRequestWithTeacherRole() throws Exception {
        mockMvc.perform(get("/teacher/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Profile")));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    public void shouldReturnForbidden_WhenRequestUnauthorized() throws Exception {
        mockMvc.perform(get("/teacher/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_WhenPostRequestTeacherId() throws Exception {
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findAllTeachers().get(0), TeacherDTO.class);
        Long id = teacherDTO.getId();

        mockMvc.perform(post("/teacher/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeHasNoErrors("teacher"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Profile")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_WhenGetRequestTeacherAdd() throws Exception {
        mockMvc.perform(post("/teacher/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Profile")));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirectToTeacherList_WhenAdminPostRequestUpdateSave() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.saveOrUpdate(user);

        mockMvc.perform(post("/teacher/update")
                .param("action", "save")
                .requestAttr("teacher", new TeacherDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "TEACHER")
    public void shouldRedirectToRoot_WhenTeacherPostRequestUpdateSave() throws Exception {
        mockMvc.perform(post("/teacher/update")
                .param("action", "save")
                .requestAttr("teacher", new TeacherDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "ADMIN")
    public void shouldRedirectToTeacherList_WhenAdminGetRequestCancelUpdate() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.saveOrUpdate(user);

        mockMvc.perform(get("/teacher/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));
    }

    @Test
    @WithMockUser(username = userName, roles = "TEACHER")
    public void shouldRedirectToRoot_WhenTeacherGetRequestCancelUpdate() throws Exception {
        mockMvc.perform(get("/teacher/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNewKey_WhenPostRequestUpdateNewKey() throws Exception {
        TeacherDTO teacherDTO = new TeacherDTO();

        mockMvc.perform(post("/teacher/update").param("action", "newKey"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attribute("teacher",
                        Matchers.hasProperty("verificationKey",
                                Matchers.not(teacherDTO.getVerificationKey()))))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Profile")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirectToTeacherList_WhenRequestDelete() throws Exception {
        List<Teacher> teachers = teacherService.findAllTeachers();
        Integer sizeBefore = teachers.size();
        TeacherDTO teacherDTO = conversionService.convert(teachers.get(0), TeacherDTO.class);
        Long id = teacherDTO.getId();

        mockMvc.perform(post("/teacher/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));

        assertThat(teacherService.findAllTeachers().size()).isEqualTo(sizeBefore - 1);
    }

    @After
    public void tearDown() {
        teacherService.deleteTeacherById(teacher.getId());
        teacherService.deleteTeacherById(noTeacher.getId());
        keyService.deleteById(key.getId());
        keyService.deleteById(keyNoTeacher.getId());
        userService.deleteUser(user);
    }
}