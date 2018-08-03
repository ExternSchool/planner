package io.github.externschool.planner.controller;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.config.WebConfig;
import io.github.externschool.planner.converter.LocalDateFormatter;
import io.github.externschool.planner.converter.SchoolSubjectFormatter;
import io.github.externschool.planner.converter.TeacherDTOToTeacher;
import io.github.externschool.planner.converter.TeacherToTeacherDTO;
import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.UserFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.service.ScheduleEventTypeService;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static io.github.externschool.planner.factories.UserFactory.USER_EMAIL;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ScheduleController.class)
@MockBeans({
        @MockBean(LocalDateFormatter.class),
        @MockBean(SchoolSubjectFormatter.class),
        @MockBean(TeacherDTOToTeacher.class),
        @MockBean(TeacherToTeacherDTO.class),
})
@ContextConfiguration(classes={PlannerApplication.class, WebConfig.class})
public class ScheduleControllerTest {

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private ScheduleEventTypeService eventTypeService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(value = "user@email.com", roles = "TEACHER")
    public void shouldDisplayFormCreateNewScheduleEvent() throws Exception {

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();

        when(this.eventTypeService.loadEventTypes()).thenReturn(Collections.singletonList(eventType));

        this.mvc.perform(get("/schedule-events/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newEvent", "eventTypes"))
                .andExpect(model().attribute("eventTypes", hasItem(eventType)))
                .andExpect(view().name("scheduleEvents/formCreateScheduleEvent"));
    }

    @Test
    @WithMockUser("user@email.com")
    public void shouldRedirectToListEventAfterSuccessfulCreateNewEvent() throws Exception {

        final ScheduleEventReq req = ScheduleEventFactory.createScheduleEventReq();

        final User user = UserFactory.createUser();
        when(this.userService.findUserByEmail(eq(USER_EMAIL))).thenReturn(user);

        final ScheduleEvent event = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        when(this.scheduleService.createEvent(eq(user), eq(req))).thenReturn(event);

        this.mvc.perform(
                post("/schedule-events/new")
                        .param("title", req.getTitle())
                        .param("description", req.getDescription())
                        .param("location", req.getLocation())
                        .param("startOfEvent", req.getStartOfEvent().toString())
                        .param("endOfEvent", req.getEndOfEvent().toString())
                        .param("eventType", req.getEventType())
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule-events"));
    }

    // TODO add tests for the validation checking
    // TODO add tests and implementation for displaying list events for current user
    // TODO add tests and implementation for displaying list events for all users
    // TODO add conversion service that loads event type by event type name
}
