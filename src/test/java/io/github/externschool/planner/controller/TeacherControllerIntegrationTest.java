package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.service.TeacherService;
import org.hamcrest.Matchers;
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

import javax.net.ssl.SSLContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private ConversionService conversionService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnTeacherListTemplate_WhenGetRequestRootWithAdminRole() throws Exception {
        mockMvc.perform(get("/teacher/"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_list"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teachers List")));
    }

    //TODO Think about should a Teacher have access to /teacher/ folder when has no access to the link in the header
    @Test
    @WithMockUser(roles = "TEACHER")
    public void shouldReturnTeacherListTemplate_WhenRequestWithTeacherRole() throws Exception {
        mockMvc.perform(get("/teacher/"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_list"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teachers List")));
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
        TeacherDTO teacherDTO = conversionService
                .convert(teacherService.findAllTeachers().get(0), TeacherDTO.class);
        Long id = teacherDTO.getId();

        mockMvc.perform(post("/teacher/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attribute("teacher", teacherDTO))
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
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirectToTeacherList_WhenPostRequestUpdateSave() throws Exception {
        mockMvc.perform(post("/teacher/update")
                .param("action", "save")
                .requestAttr("teacher", new TeacherDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));
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
    public void shouldRedirectToTeacherList_WhenGetRequestCancelUpdate() throws Exception {
        mockMvc.perform(get("/teacher/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirectToTeacherList_WhenRequestDelete() throws Exception {
        List<Teacher> teachers = teacherService.findAllTeachers();
        Integer sizeBefore = teachers.size();
        TeacherDTO teacherDTO = conversionService.convert(teachers.get(0), TeacherDTO.class);
        Long id = teacherDTO.getId();

        mockMvc.perform(get("/teacher/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));

        assertThat(teacherService.findAllTeachers().size()).isEqualTo(sizeBefore - 1);
    }
}
