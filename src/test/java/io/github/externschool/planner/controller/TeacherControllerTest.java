package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.TeacherDTO;
import io.github.externschool.planner.emailservice.EmailService;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.entity.schedule.ScheduleTemplate;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.VerificationKeyRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.FIRST_MONDAY_OF_EPOCH;
import static io.github.externschool.planner.util.Constants.UK_COURSE_NO_TEACHER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    @Autowired private UserRepository userRepository;
    @Autowired private VerificationKeyRepository keyRepository;
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
        teacher.setOfficial("Principal");
        teacher.addSubject(subject);
        teacherService.saveOrUpdateTeacher(teacher);

        user = userService.createUser(USER_NAME,"pass", "ROLE_TEACHER");
        user.addVerificationKey(key);
        userService.save(user);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldReturnTeacherListTemplate_whenGetRequestRootWithAdminRole() throws Exception {
        mockMvc.perform(get("/teacher/"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_list"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher List")));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    public void shouldReturnForbidden_whenRequestUnauthorized() throws Exception {
        mockMvc.perform(get("/teacher/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnTeacherList_whenGetTeacherWithSearchWhichDoMatch() throws Exception {
        mockMvc.perform(get("/teacher/").param("search", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_list"))
                .andExpect(content().string(Matchers.containsString("Teacher List")))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attribute("teachers",
                        Matchers.hasItem(
                                Matchers.<Person> hasProperty("firstName",
                                        Matchers.equalToIgnoringCase(teacher.getFirstName())))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnEmptyList_whenGetTeacherWithSearchWhichNotMatch() throws Exception {
        mockMvc.perform(get("/teacher/").param("search", "RequestDoesNotMatch"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_list"))
                .andExpect(content().string(Matchers.containsString("Teacher List")))
                .andExpect(model().attribute("teachers", Matchers.empty()));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldReturnTeacherProfileTemplate_whenRequestWithTeacherRole() throws Exception {
        mockMvc.perform(get("/teacher/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Profile")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldRedirect_whenDisplayTeacherVisitorsToTeacher() throws Exception {
        mockMvc.perform(get("/teacher/visitors"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/visitors"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER"})
    public void shouldReturnTemplate_whenDisplayTeacherVisitors() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/visitors"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_visitors"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("guests"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Visitors")));
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_whenPostRequestTeacherId() throws Exception {
        TeacherDTO teacherDTO = conversionService.convert(teacherService.findAllTeachers().get(0), TeacherDTO.class);
        Long id = Optional.ofNullable(teacherDTO).map(TeacherDTO::getId).orElse(0L);

        mockMvc.perform(post("/teacher/{id}", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeHasNoErrors("teacher"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Profile")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnModelAndView_whenGetRequestTeacherAdd() throws Exception {
        mockMvc.perform(post("/teacher/add").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Teacher Profile")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldNotAddNewKeyAndNewUser_whenTeacherPostUpdateSaveExistingTeacher() throws Exception {
        VerificationKey key = keyService.saveOrUpdateKey(new VerificationKey());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");
        map.add("verificationKey", key.getValue());

        long userNumber = userRepository.count();
        long keyNumber = keyRepository.count();

        mockMvc.perform(post("/teacher/update")
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
    @Transactional
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldNotAddNewKeyAndNewUser_whenAdminPostUpdateSaveExistingTeacher() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        VerificationKey verificationKey = keyService.saveOrUpdateKey(new VerificationKey());
        User keyUser = userService.createUser("user@u", "pass", "ROLE_TEACHER");
        keyUser.addVerificationKey(verificationKey);
        userService.save(keyUser);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");
        map.add("verificationKey", verificationKey.getValue());
        map.add("official", "");

        long userNumber = userRepository.count();
        long keyNumber = keyRepository.count();

        mockMvc.perform(post("/teacher/update")
                .param("action", "save")
                .params(map).with(csrf()));

        assertThat(userRepository.count())
                .isEqualTo(userNumber);

        assertThat(keyRepository.count())
                .isEqualTo(keyNumber);

        assertThat(keyRepository.findAll())
                .contains(verificationKey);

        teacherService.deleteTeacherById(verificationKey.getPerson().getId());
        keyService.deleteById(verificationKey.getId());
        userService.deleteUser(keyUser);
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldAddNewKeyAndNewUser_whenAdminPostUpdateSaveNewProfile() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("lastName", "lastName");
        map.add("firstName", "firstName");
        map.add("patronymicName", "patronymicName");
        map.add("phoneNumber", "123-4567");

        long userNumber = userRepository.count();
        long keyNumber = keyRepository.count();

        mockMvc.perform(post("/teacher/update")
                .param("action", "save")
                .params(map).with(csrf()));

        assertThat(userRepository.count())
                .isEqualTo(userNumber + 1);

        assertThat(keyRepository.count())
                .isEqualTo(keyNumber + 1);
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldRedirectToTeacherList_whenAdminPostRequestUpdateSave() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(post("/teacher/update")
                .param("action", "save")
                .requestAttr("teacher", new TeacherDTO()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldRedirectToRoot_whenTeacherPostRequestUpdateSave() throws Exception {
        mockMvc.perform(post("/teacher/update")
                .param("action", "save")
                .requestAttr("teacher", new TeacherDTO()).with(csrf()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldRedirectToTeacherList_whenAdminGetRequestCancelUpdate() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        mockMvc.perform(get("/teacher/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldRedirectToRoot_whenTeacherGetRequestCancelUpdate() throws Exception {
        mockMvc.perform(get("/teacher/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldReturnMaV_whenDisplayTeacherListFormDeleteModal() throws Exception {
        Long id = teacher.getId();

        mockMvc.perform(get("/teacher/{id}/delete-modal", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_list :: deleteTeacher"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldRedirectToTeacherList_whenRequestDelete() throws Exception {
        user.addRole(roleService.getRoleByName("ROLE_ADMIN"));
        userService.save(user);

        List<Teacher> teachers = teacherService.findAllTeachers();
        Teacher t = teachers.get(0);
        TeacherDTO teacherDTO = new TeacherDTO(
                t.getId(),
                t.getVerificationKey(),
                Optional.ofNullable(t.getVerificationKey())
                        .map(VerificationKey::getUser)
                        .map(User::getEmail)
                        .orElse(""),
                t.getFirstName(),
                t.getPatronymicName(),
                t.getLastName(),
                t.getPhoneNumber(),
                t.getOfficial(),
                t.getSubjects());

        Long id = Optional.of(teacherDTO).map(TeacherDTO::getId).orElse(0L);

        mockMvc.perform(post("/teacher/{id}/delete", id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldDeleteUser_whenRequestDeleteInvalidEmail() throws Exception {
        List<Teacher> teachers = teacherService.findAllTeachers();
        int sizeBefore = teachers.size();
        Teacher teacher = new Teacher();
        VerificationKey key = keyService.saveOrUpdateKey(new VerificationKey());
        User user = userService.createAndSaveFakeUserWithKeyAndRoleName(key, "ROLE_TEACHER");
        teacher.addVerificationKey(key);
        teacherService.saveOrUpdateTeacher(teacher);
        Long id = teacher.getId();
        String email = teacherService.findTeacherById(id).getVerificationKey().getUser().getEmail();

        assertThat(emailService.emailIsValid(email))
                .isEqualTo(false);

        mockMvc.perform(post("/teacher/{id}/delete", id).with(csrf()));

        assertThat(teacherService.findAllTeachers().size()).isEqualTo(sizeBefore);

        assertThat(userService.getUserByEmail(email))
                .isNull();
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldSetGuestRoleToUser_whenRequestDeleteValidEmail() throws Exception {
        List<Teacher> teachers = teacherService.findAllTeachers();
        int sizeBefore = teachers.size();
        Teacher teacher = new Teacher();
        VerificationKey key = keyService.saveOrUpdateKey(new VerificationKey());
        User user = userService.createAndSaveFakeUserWithKeyAndRoleName(key, "ROLE_TEACHER");
        user.setEmail("email@email.nowhere");
        userService.save(user);
        teacher.addVerificationKey(key);
        teacherService.saveOrUpdateTeacher(teacher);
        Long id = teacher.getId();
        String email = teacherService.findTeacherById(id).getVerificationKey().getUser().getEmail();

        assertThat(emailService.emailIsValid(email))
                .isEqualTo(true);

        mockMvc.perform(post("/teacher/{id}/delete", id).with(csrf()));

        assertThat(teacherService.findAllTeachers())
                .hasSize(sizeBefore);

        assertThat(userService.getUserByEmail(email))
                .isNotNull();
        assertThat(userService.userHasRole(user, "ROLE_GUEST"))
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldKeepSameNumberOfKeysAndUsers_whenPostUpdateActionNewKey() throws Exception {
        long userNumber = userRepository.count();
        long keyNumber = keyRepository.count();

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/new-key").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attribute("teacher",
                        Matchers.hasProperty("verificationKey",
                                Matchers.hasProperty("user",
                                        Matchers.not(user)))));

        assertThat(userRepository.count())
                .isEqualTo(userNumber);

        assertThat(keyRepository.count())
                .isEqualTo(keyNumber);
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = "ADMIN")
    public void shouldUnbindOldUserFromProfile_whenPostUpdateActionNewKey() throws Exception {
        mockMvc.perform(post("/teacher/" + teacher.getId() + "/new-key").with(csrf()))
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
        mockMvc.perform(post("/teacher/" + teacher.getId() + "/new-key").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attribute("teacher",
                        Matchers.hasProperty("verificationKey",
                                Matchers.not(key))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldAddAdmin_whenPostTeacherProfileFormActionAdmin() throws Exception {
        Role roleAdmin = roleService.getRoleByName("ROLE_ADMIN");
        Role roleTeacher = teacher.getVerificationKey().getUser().getRoles().stream().findAny().orElse(null);

        assertThat(teacher.getVerificationKey().getUser().getRoles())
                .hasSize(1)
                .doesNotContain(roleAdmin);

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/admin").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attribute("teacher",
                        Matchers.hasProperty("verificationKey",
                                Matchers.hasProperty("user",
                                        Matchers.hasProperty("roles",
                                                Matchers.contains(roleAdmin, roleTeacher))))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRemoveAdmin_whenPostTeacherProfileFormActionAdmin() throws Exception {
        Role roleAdmin = roleService.getRoleByName("ROLE_ADMIN");
        Role roleTeacher = user.getRoles().stream().findAny().orElse(null);
        user.addRole(roleAdmin);
        user = userService.save(user);
        assertThat(teacher.getVerificationKey().getUser().getRoles())
                .hasSize(2)
                .containsExactlyInAnyOrder(roleAdmin, roleTeacher);

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/admin").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_profile"))
                .andExpect(model().attribute("teacher",
                        Matchers.hasProperty("verificationKey",
                                Matchers.hasProperty("user",
                                        Matchers.hasProperty("roles",
                                                Matchers.contains(roleTeacher))))));
    }


    @Test
    @WithMockUser(username = USER_NAME, roles = "TEACHER")
    public void shouldRedirect_whenDisplayTeacherScheduleToTeacher() throws Exception {
        mockMvc.perform(get("/teacher/schedule"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_whenDisplayTeacherSchedule() throws Exception {
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
    public void shouldReturnTemplate_whenDisplayTeacherDeleteCurrentWeekDayModal() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/current-week/" + 0))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: cancelCurrentWeekDay"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("thisDay"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("cancelCurrentModal")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_whenProcessTeacherDeleteCurrentWeekDay() throws Exception {
        mockMvc.perform(post("/teacher/" + teacher.getId() + "/current-week/" + 0 + "/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_whenDisplayTeacherNewCurrentModal() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/new-current/" + 0))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: newCurrent"))
                .andExpect(model().attributeExists("eventTypes"))
                .andExpect(model().attributeExists("newEvent"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("thisDay"))
                .andExpect(model().attributeExists("thisDayEvents"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("newCurrentModal")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_whenProcessTeacherCurrentModalFormAddEvent() throws Exception {
        ScheduleEventType type = typeService.loadEventTypes().get(0);
        ScheduleEventDTO newEvent = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(LocalDate.now())
                .withStartTime(LocalTime.MIN)
                .withEventType(type.getName())
                .withDescription("Description")
                .build();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("date", newEvent.getDate().toString());
        map.add("description", newEvent.getDescription());
        map.add("eventType", newEvent.getEventType());
        map.add("startTime", newEvent.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/new-current/" + 0 + "/add")
                .params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_whenDisplayTeacherDeleteNextWeekDayModal() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/next-week/" + 0))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: cancelNextWeekDay"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("thisDay"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("cancelNextModal")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_whenProcessTeacherDeleteNextWeekDay() throws Exception {
        mockMvc.perform(post("/teacher/" + teacher.getId() + "/next-week/" + 0 + "/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_whenDisplayTeacherNewNextModal() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/new-next/" + 0))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: newNext"))
                .andExpect(model().attributeExists("eventTypes"))
                .andExpect(model().attributeExists("newEvent"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("thisDay"))
                .andExpect(model().attributeExists("thisDayEvents"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("newNextModal")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_whenProcessTeacherNextModalFormAddEvent() throws Exception {
        ScheduleEventType type = typeService.loadEventTypes().get(0);
        ScheduleEventDTO newEvent = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(LocalDate.now().plusDays(7))
                .withStartTime(LocalTime.MIN)
                .withEventType(type.getName())
                .withDescription("Description")
                .build();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("date", newEvent.getDate().toString());
        map.add("description", newEvent.getDescription());
        map.add("eventType", newEvent.getEventType());
        map.add("startTime", newEvent.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/new-next/" + 0 + "/add")
                .params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_whenDisplayModalFormDeleteEvent() throws Exception {
        ScheduleEventType type = typeService.loadEventTypes().get(0);
        ScheduleEventDTO newEvent = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(LocalDate.now().plusDays(7))
                .withTitle(type.getName())
                .withStartTime(LocalTime.MIN)
                .withEventType(type.getName())
                .withDescription("Description")
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(user, newEvent, 30);
        scheduleService.saveEvent(event);
        userService.save(user);
        Long eventId = event.getId();
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/event/" + eventId + "/modal").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: deleteEvent"))
                .andExpect(model().attributeExists("newEvent"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("deleteEventModal")));
    }

    @Test
    @Transactional
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_whenProcessModalFormDeleteEvent() throws Exception {
        ScheduleEventType type = typeService.loadEventTypes().get(0);
        ScheduleEventDTO newEvent = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withDate(LocalDate.now().plusDays(7))
                .withTitle(type.getName())
                .withStartTime(LocalTime.MIN)
                .withEventType(type.getName())
                .withDescription("Description")
                .build();
        ScheduleEvent event = scheduleService.createEventWithDuration(user, newEvent, 30);
        scheduleService.saveEvent(event);
        userService.save(user);
        Long eventId = event.getId();
        assertThat(teacherService.findTeacherById(teacher.getId()))
                .isEqualTo(teacher);
        assertThat(scheduleService.getEventById(eventId))
                .isNotNull();

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/event/" + eventId + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_whenDisplayTeacherNewScheduleModal() throws Exception {
        mockMvc.perform(get("/teacher/" + teacher.getId() + "/day/" + 0 + "/modal-template"))
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
    public void shouldRedirect_whenProcessTeacherScheduleModalFormAddEvent() throws Exception {
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

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/day/" + 0 + "/add-template")
                .params(map).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldReturnTemplate_whenDisplayTeacherTemplateDeleteModal() throws Exception {
        ScheduleEventDTO dto = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withEventType(typeService.loadEventTypes().get(0).getName())
                .withDate(LocalDate.now())
                .withStartTime(LocalTime.now())
                .withDescription("")
                .withTitle("")
                .withCreated(LocalDateTime.now())
                .withIsOpen(true)
                .build();
        ScheduleTemplate template = scheduleService.createTemplate(user, dto, DayOfWeek.MONDAY, 30);
        scheduleService.saveTemplate(template);
        long id = template.getId();

        mockMvc.perform(get("/teacher/" + teacher.getId() + "/template/" + id + "/delete-modal").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/teacher_schedule :: deleteTemplate"))
                .andExpect(model().attributeExists("newEvent"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("deleteTemplateModal")));
    }

    @Test
    @WithMockUser(username = USER_NAME, roles = {"TEACHER", "ADMIN"})
    public void shouldRedirect_whenProcessTeacherTemplateDelete() throws Exception {
        ScheduleEventDTO dto = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withEventType(typeService.loadEventTypes().get(0).getName())
                .withDate(LocalDate.now())
                .withStartTime(LocalTime.now())
                .withDescription("")
                .withTitle("")
                .withCreated(LocalDateTime.now())
                .withIsOpen(true)
                .build();
        ScheduleTemplate template = scheduleService.createTemplate(user, dto, DayOfWeek.MONDAY, 30);
        scheduleService.saveTemplate(template);
        long id = template.getId();

        mockMvc.perform(post("/teacher/" + teacher.getId() + "/template/" + id + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teacher/" + teacher.getId() + "/schedule"));
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
