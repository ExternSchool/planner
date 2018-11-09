package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.emailservice.EmailService;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.FIRST_MONDAY_OF_EPOCH;
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
@SpringBootTest
public class TeacherControllerTest {
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private TeacherService teacherService;
    @Autowired private SchoolSubjectService subjectService;
    @Autowired private ConversionService conversionService;
    @Autowired private VerificationKeyService keyService;
    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private ScheduleEventTypeService typeService;
    @Autowired private EmailService emailService;
    private TeacherController controller;
    private MockMvc mockMvc;

    private Teacher teacher;
    private Teacher noTeacher;
    private VerificationKey key;
    private VerificationKey keyNoTeacher;
    private User user;
    private static final String USER_NAME = "some@email.com";
    private ScheduleEvent event;

    @Before
    public void setup() {
        controller = new TeacherController(
                teacherService,
                subjectService,
                conversionService,
                keyService,
                userService,
                roleService,
                scheduleService,
                typeService,
                emailService);

        noTeacher = new Teacher();
        noTeacher.setLastName(UK_COURSE_NO_TEACHER);
        keyNoTeacher = new VerificationKey();
        keyService.saveOrUpdateKey(keyNoTeacher);
        noTeacher.addVerificationKey(keyNoTeacher);
        teacherService.saveOrUpdateTeacher(noTeacher);

        SchoolSubject subject = new SchoolSubject();
        subjectService.saveOrUpdateSubject(subject);

        key = new VerificationKey();
        keyService.saveOrUpdateKey(key);
        teacher = new Teacher();
        teacher.setFirstName("First");
        teacher.setPatronymicName("Patron");
        teacher.setLastName("Last");
        teacher.setPhoneNumber("(000)000-0000");
        teacher.addVerificationKey(key);
        teacher.setOfficer("Principal");
        teacher.addSubject(subject);
        teacherService.saveOrUpdateTeacher(teacher);

        user = userService.createUser(USER_NAME,"pass", "ROLE_TEACHER");
        user.addVerificationKey(key);
        userService.saveOrUpdate(user);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
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
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
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
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldRedirect_WhenDisplayTeacherVisitorsToTeacher() throws Exception {
        mockMvc.perform(get("/teacher/visitors"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/visitors"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER"})
    public void shouldReturnTemplate_WhenDisplayTeacherVisitors() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/visitors"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_visitors"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("visitors"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Visitors")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldRedirect_WhenDisplayTeacherScheduleToTeacher() throws Exception {
        mockMvc.perform(get("/teacher/schedule"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_WhenDisplayTeacherSchedule() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("currentWeek"))
                .andExpect(model().attributeExists("nextWeek"))
                .andExpect(model().attributeExists("currentWeekEvents"))
                .andExpect(model().attributeExists("nextWeekEvents"))
                .andExpect(model().attributeExists("standardWeekEvents"))
                .andExpect(model().attributeExists("newEvent"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Schedule")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_WhenProcessTeacherEventDelete() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.saveOrUpdate(user);
        ScheduleEventDTO dto = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withEventType(typeService.loadEventTypes().get(0).getName())
                .withDate(LocalDate.now())
                .withStartTime(LocalTime.now())
                .withDescription("")
                .withTitle("")
                .withCreated(LocalDateTime.now())
                .withIsOpen(true)
                .build();
        event = scheduleService.createEventWithDuration(user, dto, 30);
        long id = event.getId();

        mockMvc.perform(get("/teacher/" + teacher.getId() + "/event/" + id + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_WhenDisplayTeacherNewScheduleModal() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/new-schedule/" + 0))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: newSchedule"))
                .andExpect(model().attributeExists("eventTypes"))
                .andExpect(model().attributeExists("newEvent"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("thisDay"))
                .andExpect(model().attributeExists("thisDayEvents"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("newScheduleModal")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_WhenDisplayTeacherDeleteCurrentWeekDayModal() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/current-week/" + 0))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: deleteCurrentWeekDay"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("thisDay"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("deleteCurrentModal")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_WhenProcessTeacherDeleteCurrentWeekDay() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/current-week/" + 0 + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }


    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_WhenDisplayTeacherDeleteNextWeekDayModal() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/next-week/" + 0))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: deleteNextWeekDay"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("thisDay"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("deleteNextModal")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_WhenProcessTeacherDeleteNextWeekDay() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/next-week/" + 0 + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }


    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_WhenProcessTeacherScheduleModalFormAddEvent() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.saveOrUpdate(user);
        ScheduleEventDTO newEvent = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(FIRST_MONDAY_OF_EPOCH.plusDays(0))
                .withDescription(typeService.loadEventTypes().get(0).getName())
                .withEventType(typeService.loadEventTypes().get(0).getName())
                .withStartTime(LocalTime.now())
                .build();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("date", newEvent.getDate().toString());
        map.add("description", newEvent.getDescription());
        map.add("eventType", newEvent.getEventType());
        map.add("startTime", newEvent.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/new-schedule/" + 0 + "/add")
                .params(map))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));

        event = scheduleService.getActualEventsByOwnerAndDate(user, newEvent.getDate()).get(0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_WhenPostRequestTeacherId() throws Exception {
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findAllTeachers().get(0), TeacherDTO.class);
        Long id = Optional.ofNullable(teacherDTO).map(TeacherDTO::getId).orElse(0L);

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
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
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
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldRedirectToRoot_WhenTeacherPostRequestUpdateSave() throws Exception {
        mockMvc.perform(post("/teacher/update")
                .param("action", "save")
                .requestAttr("teacher", new TeacherDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldRedirectToTeacherList_WhenAdminGetRequestCancelUpdate() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.saveOrUpdate(user);

        mockMvc.perform(get("/teacher/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldRedirectToRoot_WhenTeacherGetRequestCancelUpdate() throws Exception {
        mockMvc.perform(get("/teacher/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldRedirectToTeacherList_WhenRequestDelete() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.saveOrUpdate(user);
        List<Teacher> teachers = teacherService.findAllTeachers();
        int sizeBefore = teachers.size();
        TeacherDTO teacherDTO = conversionService.convert(teachers.get(0), TeacherDTO.class);
        Long id = Optional.ofNullable(teacherDTO).map(TeacherDTO::getId).orElse(0L);

        mockMvc.perform(post("/teacher/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));

        assertThat(teacherService.findAllTeachers().size()).isEqualTo(sizeBefore - 1);
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldUnbindOldUserFromProfile_whenPostUpdateActionNewKey() throws Exception {
        mockMvc.perform(post("/teacher/" + noTeacher.getId() + "/new-key"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attribute("teacher",
                        Matchers.hasProperty("verificationKey",
                                Matchers.hasProperty("user",
                                        Matchers.not(user)))));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldSetNewKeyToDTO_whenPostUpdateActionNewKey() throws Exception {
        mockMvc.perform(post("/teacher/" + noTeacher.getId() + "/new-key"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attribute("teacher",
                        Matchers.hasProperty("verificationKey",
                                Matchers.not(key))));
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
