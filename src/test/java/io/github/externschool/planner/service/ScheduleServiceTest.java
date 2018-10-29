package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.UserCannotCreateEventException;
import io.github.externschool.planner.factories.RolesFactory;
import io.github.externschool.planner.factories.UserFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 * @author Benkoff (mailto.benkoff@gmail.com)
 */
public class ScheduleServiceTest {
    @Mock private ScheduleEventTypeRepository eventTypeRepo;
    @Mock private ScheduleEventRepository eventRepo;
    private ScheduleService scheduleService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.scheduleService = new ScheduleServiceImpl(this.eventRepo, this.eventTypeRepo);
    }

    @Test
    public void shouldCreateNewScheduleEventIfUserValidForThisEventType() {
        ScheduleEventReq eventReq = ScheduleEventFactory.createScheduleEventReq();

        User user = UserFactory.createUser();
        user.getRoles().add(RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT));

        ScheduleEvent expectedEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        expectedEvent.setId(null);

        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg()).when(this.eventRepo).save(any(ScheduleEvent.class));

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        eventType.getCreators().add(RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT));
        when(this.eventTypeRepo.findByName(eq(eventReq.getEventType()))).thenReturn(eventType);

        ScheduleEvent event = this.scheduleService.createEvent(user, eventReq);

        assertThat(event)
                .isNotNull()
                .isEqualTo(expectedEvent);

    }

    @Test(expected = UserCannotCreateEventException.class)
    public void shouldCreateNewScheduleEventIfUserInValidForThisEventType() {
        User user = UserFactory.createUser();
        ScheduleEventReq eventReq = ScheduleEventFactory.createScheduleEventReq();

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        when(this.eventTypeRepo.findByName(eq(eventReq.getEventType()))).thenReturn(eventType);

        this.scheduleService.createEvent(user, eventReq);
    }

    @Test
    public void shouldReturnCurrentWeekFirstDay() {
        LocalDate firstDayCurrentWeek = scheduleService.getCurrentWeekFirstDay();
        LocalDate firstDayNextWeek = scheduleService.getNextWeekFirstDay();

        assertThat(firstDayCurrentWeek)
                .isNotNull()
                .isInstanceOf(LocalDate.class)
                .isBefore(firstDayNextWeek)
                .isBetween(LocalDate.now().minus(Period.of(0,0,6)), LocalDate.now());
    }

    @Test
    public void shouldReturnNextWeekFirstDay() {
        LocalDate firstDayCurrentWeek = scheduleService.getCurrentWeekFirstDay();
        LocalDate firstDayNextWeek = scheduleService.getNextWeekFirstDay();

        assertThat(firstDayNextWeek)
                .isNotNull()
                .isInstanceOf(LocalDate.class)
                .isAfter(firstDayCurrentWeek)
                .isBetween(LocalDate.now().plus(Period.of(0, 0, 1)),
                        LocalDate.now().plus(Period.of(0, 0, 7)));
    }

    @Test
    public void shouldReturnWeekStartingFirstDay() {
        LocalDate firstDay = LocalDate.now();
        List<LocalDate> week = scheduleService.getWeekStartingFirstDay(firstDay);

        assertThat(week)
                .hasSize(7)
                .containsSequence(
                        firstDay,
                        firstDay.plus(Period.of(0, 0 ,1)),
                        firstDay.plus(Period.of(0, 0 ,2)),
                        firstDay.plus(Period.of(0, 0 ,3)),
                        firstDay.plus(Period.of(0, 0 ,4)),
                        firstDay.plus(Period.of(0, 0 ,5)),
                        firstDay.plus(Period.of(0, 0 ,6)));
    }

    @Test
    public void shouldReturnListEvents_whenGetEventsByOwnerAndDate() {
        ScheduleEvent eventOne = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        eventOne.setId(2L);
        ScheduleEvent eventTwo = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        List<ScheduleEvent> expectedEvents = Arrays.asList(eventOne, eventTwo);

        User owner = new User();
        LocalDate date = LocalDate.of(2018, 6, 7);
        Mockito.when(
                eventRepo.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(owner, date.atStartOfDay(), date.atTime(LocalTime.MAX)))
                .thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.getEventsByOwnerAndDate(owner, date);

        assertThat(actualEvents)
                .isNotNull()
                .containsSequence(expectedEvents);
    }


    @Test
    public void shouldReturnListEvents_whenGetEventsByOwner() {
        ScheduleEvent eventOne = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        eventOne.setId(2L);
        ScheduleEvent eventTwo = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        List<ScheduleEvent> expectedEvents = Arrays.asList(eventOne, eventTwo);

        User owner = new User();
        Mockito.when(
                eventRepo.findAllByOwner(owner))
                .thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.getEventsByOwner(owner);

        assertThat(actualEvents)
                .isNotNull()
                .containsSequence(expectedEvents);
    }

    @Test
    public void shouldReturnEventById() {
        long id = 100500L;
        ScheduleEvent expectedEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        expectedEvent.setId(id);

        Mockito.when(eventRepo.getOne(id))
                .thenReturn(expectedEvent);

        ScheduleEvent actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent)
                .isNotNull()
                .isEqualTo(expectedEvent);
    }

    @Test
    public void shouldDeleteEventById() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);

        when(this.eventRepo.getOne(id))
                .thenReturn(anEvent)
                .thenReturn(null);

        ScheduleEvent actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent)
                .isNotNull()
                .isEqualTo(anEvent);

        scheduleService.deleteEvent(id);
        actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent)
                .isNull();
    }
}
