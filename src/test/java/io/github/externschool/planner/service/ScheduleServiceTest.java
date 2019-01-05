package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.emailservice.EmailService;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.entity.schedule.ScheduleHoliday;
import io.github.externschool.planner.entity.schedule.ScheduleTemplate;
import io.github.externschool.planner.exceptions.UserCanNotHandleEventException;
import io.github.externschool.planner.factories.RolesFactory;
import io.github.externschool.planner.factories.UserFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.schedule.ParticipantRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import io.github.externschool.planner.repository.schedule.ScheduleHolidayRepository;
import io.github.externschool.planner.repository.schedule.ScheduleTemplateRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ScheduleServiceTest {
    @Mock private ScheduleEventRepository eventRepository;
    @Mock private ScheduleEventTypeRepository eventTypeRepo;
    @Mock private ParticipantRepository participantRepository;
    @Mock private EmailService emailService;
    @Mock private ScheduleHolidayRepository holidayRepository;
    @Mock private ScheduleTemplateRepository templateRepository;
    @Mock private UserRepository userRepository;
    private ScheduleService scheduleService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.scheduleService = new ScheduleServiceImpl(
                this.eventRepository,
                this.eventTypeRepo,
                this.participantRepository,
                this.emailService,
                this.holidayRepository,
                this.templateRepository,
                this.userRepository);
    }

    @Test
    public void shouldCreateNewScheduleEvent_ifUserValidForThisEventType() {
        ScheduleEventReq eventReq = ScheduleEventFactory.createScheduleEventReq();

        User user = UserFactory.createUser();
        user.addRole(RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT));

        ScheduleEvent expectedEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        expectedEvent.setId(null);

        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg()).when(this.eventRepository).save(any(ScheduleEvent.class));

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        eventType.addOwner(RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT));
        when(this.eventTypeRepo.findByName(eq(eventReq.getEventType()))).thenReturn(eventType);

        ScheduleEvent event = this.scheduleService.createEvent(user, eventReq);
        event.setCreatedAt(expectedEvent.getCreatedAt());

        assertThat(event)
                .isNotNull()
                .isEqualTo(expectedEvent);
    }

    @Test(expected = UserCanNotHandleEventException.class)
    public void shouldThrowException_whenUserInvalidForThisEventType() {
        User user = UserFactory.createUser();
        ScheduleEventReq eventReq = ScheduleEventFactory.createScheduleEventReq();

        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        when(this.eventTypeRepo.findByName(eq(eventReq.getEventType()))).thenReturn(eventType);

        this.scheduleService.createEvent(user, eventReq);
    }

    @Test
    public void shouldReturnEvent_whenCreateEventWithDuration() {
        long id = 100500L;
        ScheduleEvent expectedEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        expectedEvent.setId(id);
        expectedEvent.setOpen(true);
        User user = expectedEvent.getOwner();
        ScheduleEventType type = ScheduleEventTypeFactory.createScheduleEventType();
        Role allowedRole = new Role(RolesFactory.ROLE_ALLOWED_CREATE_EVENT);
        type.addOwner(allowedRole);

        assertThat(type.getOwners())
                .contains(allowedRole);

        ScheduleEventDTO dto = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withEventType(type.getName())
                .withDate(expectedEvent.getStartOfEvent().toLocalDate())
                .withStartTime(expectedEvent.getStartOfEvent().toLocalTime())
                .withDescription(expectedEvent.getDescription())
                .withTitle(expectedEvent.getTitle())
                .withIsOpen(true)
                .build();

        when(eventTypeRepo.findByName(type.getName()))
                .thenReturn(type);
        when(userRepository.save(user))
                .thenReturn(user);

        ScheduleEvent event = scheduleService.createEventWithDuration(user, dto, 30);

        assertThat(event)
                .isEqualToIgnoringGivenFields(expectedEvent,
                        "id", "location", "endOfEvent", "createdAt")
                .hasFieldOrPropertyWithValue("endOfEvent",
                        event.getStartOfEvent().plus(Duration.of(30, ChronoUnit.MINUTES)));
    }

    @Test
    public void shouldReturnEvent_whenGetEventById() {
        long id = 100500L;
        ScheduleEvent expectedEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        expectedEvent.setId(id);

        Mockito.when(eventRepository.getOne(id))
                .thenReturn(expectedEvent);

        ScheduleEvent actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent)
                .isNotNull()
                .isEqualTo(expectedEvent);
    }

    @Test
    public void shouldReturnEvent_whenSaveEvent() {
        long id = 100500L;
        ScheduleEvent expectedEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        expectedEvent.setId(id);

        Mockito.when(eventRepository.save(expectedEvent))
                .thenReturn(expectedEvent);

        ScheduleEvent actualEvent = scheduleService.saveEvent(expectedEvent);

        assertThat(actualEvent)
                .isNotNull()
                .isEqualTo(expectedEvent);
    }

    @Test
    public void shouldReturnList_whenGetActualEventsByOwnerAndDate() {
        ScheduleEvent eventOne = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        ScheduleEvent eventTwo = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        List<ScheduleEvent> expectedEvents = Arrays.asList(eventOne, eventTwo);

        User owner = new User();
        LocalDate date = LocalDate.of(2018, 6, 7);
        Mockito.when(
                eventRepository.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(
                        owner,
                        date.atStartOfDay(),
                        date.atTime(LocalTime.MAX)))
                .thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.getNonCancelledEventsByOwnerAndDate(owner, date);

        assertThat(actualEvents)
                .isNotNull()
                .containsSequence(expectedEvents)
                .noneMatch(ScheduleEvent::isCancelled);
    }

    @Test
    public void shouldReturnList_whenGetEventsByOwner() {
        ScheduleEvent eventOne = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        eventOne.setId(2L);
        ScheduleEvent eventTwo = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        List<ScheduleEvent> expectedEvents = Arrays.asList(eventOne, eventTwo);

        User owner = new User();
        Mockito.when(
                eventRepository.findAllByOwner(owner))
                .thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.getEventsByOwner(owner);

        assertThat(actualEvents)
                .isNotNull()
                .containsSequence(expectedEvents);
    }

    @Test
    public void shouldReturnListEvents_whenGetEventsByOwnerStartingBetweenDates() {
        ScheduleEvent eventOne = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        ScheduleEvent eventTwo = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        List<ScheduleEvent> expectedEvents = Arrays.asList(eventOne, eventTwo);

        User owner = new User();
        LocalDate firstDate = LocalDate.of(2018, 6, 1);
        LocalDate lastDate = LocalDate.of(2018, 6, 30);
        Mockito.when(
                eventRepository.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(
                        owner,
                        firstDate.atStartOfDay(),
                        lastDate.atTime(LocalTime.MAX)))
                .thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents =
                scheduleService.getEventsByOwnerStartingBetweenDates(owner, firstDate, lastDate);

        assertThat(actualEvents)
                .isNotNull()
                .containsSequence(expectedEvents);
    }

    @Test
    public void shouldReturnList_whenGetEventsByType() {
        ScheduleEvent eventOne = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        ScheduleEvent eventTwo = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        List<ScheduleEvent> expectedEvents = Arrays.asList(eventOne, eventTwo);

        ScheduleEventType type = new ScheduleEventType("Type", 1);
        eventOne.setType(type);
        eventTwo.setType(type);

        Mockito.when(eventTypeRepo.findByName("Type"))
                .thenReturn(type);
        Mockito.when(eventRepository.findAllByType(type))
                .thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.getEventsByType(type);

        assertThat(actualEvents)
                .isNotNull()
                .containsSequence(expectedEvents);
    }

    @Test
    public void shouldReturnCancelledEvent_whenCancelEventByIdAndSave() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);

        when(this.eventRepository.findById(id))
                .thenReturn(Optional.of(anEvent));
        when(this.eventRepository.getOne(id))
                .thenReturn(anEvent);

        scheduleService.findEventByIdSetCancelledNotOpenAndSave(id);
        ScheduleEvent actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent)
                .isNotNull()
                .hasFieldOrPropertyWithValue("isCancelled", true);
    }

    @Test
    public void shouldReturnOpenEvent_whenfFindEventByIdSetOpenAndSave() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);
        anEvent.setOpen(false);

        when(this.eventRepository.findById(id))
                .thenReturn(Optional.of(anEvent));
        when(this.eventRepository.getOne(id))
                .thenReturn(anEvent);

        scheduleService.findEventByIdSetOpenByStateAndSave(id, true);
        ScheduleEvent actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent)
                .isNotNull()
                .hasFieldOrPropertyWithValue("isOpen", true);
    }

    @Test
    public void shouldReturnNull_whenDeleteEventById() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);
        User participant = new User("participant@email.com", "pass");

        when(this.eventRepository.getOne(id))
                .thenReturn(anEvent)
                .thenReturn(null);
        when(this.eventRepository.findById(id))
                .thenReturn(Optional.of(anEvent));

        ScheduleEvent actualEvent = scheduleService.getEventById(id);
        scheduleService.addParticipant(participant, actualEvent);

        assertThat(actualEvent)
                .isNotNull()
                .isEqualTo(anEvent)
                .hasFieldOrPropertyWithValue("owner", anEvent.getOwner());

        scheduleService.deleteEventById(id);
        actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent)
                .isNull();
        assertThat(anEvent)
                .hasFieldOrPropertyWithValue("owner", null)
                .hasFieldOrPropertyWithValue("participants", Collections.emptySet());
    }

    @Test
    @Repeat(10)
    public void shouldSendEmail_whenCancelEventsAndMailToParticipants(){
        long id = 1L;
        Role userRole = RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT);
        ScheduleEvent event = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        event.setId(id);
        event.setOpen(true);
        event.getType().addParticipant(userRole);
        User user = new User("participant@email.com", "pass");
        user.setId(++id);
        user.addRole(userRole);
        Participant participant = new Participant(user, event);
        participant.setId(++id);
        ScheduleEvent event2 = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        BeanUtils.copyProperties(event, event2);
        event2.setId(++id);
        List<ScheduleEvent> events = Arrays.asList(event, event2);
        assertThat(events)
                .containsExactly(event, event2);

        scheduleService.cancelEventsAndMailToParticipants(events);

        verify(eventRepository, times(2)).findById(event.getId());
        verify(eventRepository, times(2)).findById(event2.getId());
        verify(emailService, after(100).times(1)).sendCancelEventMail(event);
        verify(emailService, after(100).times(1)).sendCancelEventMail(event2);
    }

    @Test(expected = UserCanNotHandleEventException.class)
    public void shouldThrowException_whenAddOwnerWithoutRole() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);
        User owner = anEvent.getOwner();
        anEvent.setOwner(null);
        owner.removeOwnEvent(anEvent);

        when(this.eventRepository.getOne(id))
                .thenReturn(anEvent);

        ScheduleEvent actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent.getOwner())
                .isNull();
        assertThat(actualEvent.getType().getOwners())
                .doesNotContain(RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT));

        scheduleService.addOwner(owner, actualEvent);
    }

    @Test
    public void shouldReturnEvent_whenAddOwnerWithRole() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);
        User owner = anEvent.getOwner();
        anEvent.setOwner(null);
        owner.removeOwnEvent(anEvent);
        Role allowedRole = RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT);
        anEvent.getType().addOwner(allowedRole);

        when(this.eventRepository.getOne(id))
                .thenReturn(anEvent);

        ScheduleEvent actualEvent = scheduleService.getEventById(id);

        assertThat(actualEvent.getOwner())
                .isNull();
        assertThat(actualEvent.getType().getOwners())
                .contains(allowedRole);

        scheduleService.addOwner(owner, actualEvent);

        assertThat(actualEvent)
                .isNotNull()
                .isEqualTo(anEvent)
                .hasFieldOrPropertyWithValue("owner", owner);

        assertThat(owner.getOwnEvents())
                .isNotEmpty()
                .contains(actualEvent);
    }

    @Test
    public void shouldRemoveOwner_whenRemoveOwner() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);
        User owner = anEvent.getOwner();

        assertThat(anEvent.getOwner())
                .isEqualTo(owner);

        scheduleService.removeOwner(anEvent);

        assertThat(anEvent)
                .isNotNull()
                .isEqualTo(anEvent)
                .hasFieldOrPropertyWithValue("owner", null);

        assertThat(owner.getOwnEvents())
                .isEmpty();
    }

    @Test
    public void shouldAddParticipant_whenAddParticipantToOpenEvent() {
        long id = 100500L;
        Role userRole = RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT);
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);
        anEvent.setOpen(true);
        anEvent.getType().addParticipant(userRole);
        User user = new User("participant@email.com", "pass");
        user.addRole(userRole);
        Participant expectedParticipant = new Participant(user, anEvent);

        when(this.eventRepository.getOne(id))
                .thenReturn(anEvent);
        when(participantRepository.save(expectedParticipant))
                .thenReturn(expectedParticipant);

        ScheduleEvent actualEvent = scheduleService.getEventById(id);
        scheduleService.addParticipant(user, actualEvent);
        Participant participant = new ArrayList<>(actualEvent.getParticipants()).get(0);

        assertThat(actualEvent.getParticipants())
                .isNotEmpty()
                .contains(participant);

        assertThat(user.getParticipants())
                .isNotEmpty()
                .contains(participant);

        assertThat(participant)
                .hasFieldOrPropertyWithValue("user", user);
    }

    @Test
    public void shouldNotAddParticipant_whenAddParticipantToClosedEvent() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);
        anEvent.setOpen(false);
        User user = new User("participant@email.com", "pass");

        when(this.eventRepository.getOne(id))
                .thenReturn(anEvent);

        ScheduleEvent actualEvent = scheduleService.getEventById(id);
        scheduleService.addParticipant(user, actualEvent);

        assertThat(actualEvent.getParticipants())
                .isEmpty();
        assertThat(user.getParticipants())
                .isEmpty();
    }

    @Test
    public void shouldReturnValue_whenFindParticipantByUserAndEvent() {
        long id = 100500L;
        ScheduleEvent event = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        event.setId(id);
        User user = new User("participant@email.com", "pass");
        Optional<Participant> participant = Optional.of(new Participant(user, event));

        when(participantRepository.findParticipantByUserAndEvent(user, event))
                .thenReturn(participant);

        assertThat(scheduleService.findParticipantByUserAndEvent(user, event))
                .isNotEmpty()
                .get()
                .isEqualToComparingFieldByField(participant.get());
    }

    @Test
    public void shouldReturnEmpty_whenFindParticipantByUserAndEvent() {
        long id = 100500L;
        ScheduleEvent event = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        event.setId(id);
        User user = new User("wrong@email.com", "pass");

        when(participantRepository.findParticipantByUserAndEvent(user, event))
                .thenReturn(Optional.empty());

        assertThat(scheduleService.findParticipantByUserAndEvent(user, event))
                .isEmpty();
    }

    @Test
    public void shouldReturnList_whenGetParticipantsByUser() {
        long id = 100500L;
        ScheduleEvent event = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        event.setId(id);
        User user = new User("participant@email.com", "pass");
        List<Participant> participants = Collections.singletonList(new Participant(user, event));

        when(participantRepository.getAllByUser(user))
                .thenReturn(participants);

        assertThat(scheduleService.getParticipantsByUser(user))
                .isNotEmpty()
                .containsExactly(participants.get(0));
    }

    @Test
    public void shouldRemoveParticipant_whenRemoveParticipant() {
        long id = 100500L;
        ScheduleEvent anEvent = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        anEvent.setId(id);
        User participant = new User("participant@email.com", "pass");

        when(this.eventRepository.getOne(id))
                .thenReturn(anEvent)
                .thenReturn(null);
        when(this.eventRepository.findById(id))
                .thenReturn(Optional.of(anEvent));

        ScheduleEvent actualEvent = scheduleService.getEventById(id);
        scheduleService.addParticipant(participant, actualEvent);

        assertThat(actualEvent)
                .isNotNull()
                .isEqualTo(anEvent)
                .hasFieldOrPropertyWithValue("participants", Collections.emptySet());
    }

    @Test
    public void shouldReturnCurrentWeekFirstDay() {
        LocalDate date = LocalDate.now();
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(2L);
        }
        LocalDate start = date.with(WeekFields.of(LOCALE).dayOfWeek(), 1);
        LocalDate end = date.with(WeekFields.of(LOCALE).dayOfWeek(), 7);

        LocalDate firstDayCurrentWeek = scheduleService.getCurrentWeekFirstDay();
        LocalDate firstDayNextWeek = scheduleService.getNextWeekFirstDay();

        assertThat(firstDayCurrentWeek)
                .isNotNull()
                .isInstanceOf(LocalDate.class)
                .isBefore(firstDayNextWeek)
                .isBetween(start, end);
    }

    @Test
    public void shouldReturnNextWeekFirstDay() {
        LocalDate date = LocalDate.now().plus(Period.of(0, 0, 7));
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(2L);
        }
        LocalDate start = date.with(WeekFields.of(LOCALE).dayOfWeek(), 1);
        LocalDate end = date.with(WeekFields.of(LOCALE).dayOfWeek(), 7);

        LocalDate firstDayCurrentWeek = scheduleService.getCurrentWeekFirstDay();
        LocalDate firstDayNextWeek = scheduleService.getNextWeekFirstDay();

        assertThat(firstDayNextWeek)
                .isNotNull()
                .isInstanceOf(LocalDate.class)
                .isAfter(firstDayCurrentWeek)
                .isBetween(start, end);
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
    public void ahouldReturnScheduleHoliday_whenSaveHoliday() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1L);
        ScheduleHoliday scheduleHoliday = new ScheduleHoliday(start, end);
        Mockito.when(holidayRepository.save(scheduleHoliday))
                .thenReturn(scheduleHoliday);

        assertThat(scheduleService.saveHoliday(start, end))
                .isEqualTo(scheduleHoliday);
    }

    @Test
    public void shouldReturnScheduleHoliday_whenFindHolidayById() {
        ScheduleHoliday scheduleHoliday = new ScheduleHoliday(LocalDate.now(), LocalDate.now().plusDays(1L));
        Long id = 100500L;
        Mockito.when(holidayRepository.findById(id))
                .thenReturn(Optional.of(scheduleHoliday));

        assertThat(scheduleService.findHolidayById(id))
                .isNotEmpty()
                .contains(scheduleHoliday);
    }

    @Test
    public void shouldReturnEmptyOptional_whenFindHolidayById() {
        Mockito.when(holidayRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThat(scheduleService.findHolidayById(100500L))
                .isEmpty();
    }

    @Test
    public void shouldInvoke_whenDeleteHoliday() {
        ScheduleHoliday scheduleHoliday = new ScheduleHoliday(LocalDate.now(), LocalDate.now().plusDays(1L));
        Long id = 100500L;
        scheduleHoliday.setId(id);

        scheduleService.deleteHolidayById(id);

        verify(holidayRepository, times(1)).deleteById(id);
    }

    @Test
    public void shouldReturnList_whenFindHolidaysBetweenDates() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1L);
        List<ScheduleHoliday> holidays = Arrays.asList(
                        new ScheduleHoliday(start, null),
                        new ScheduleHoliday(end, null));

        Mockito.when(holidayRepository.findAllByHolidayDateBetween(start, end))
                .thenReturn(holidays);

        assertThat(scheduleService.getHolidaysBetweenDates(start, end))
                .containsExactlyInAnyOrderElementsOf(holidays);
    }

    @Test
    public void shouldReturnScheduleTemplate_whenSaveTemplate() {
        ScheduleTemplate template = ScheduleTemplate.builder().build();

        when(templateRepository.save(template))
                .thenReturn(template);

        assertThat(scheduleService.saveTemplate(template))
                .isEqualTo(template);
    }

    @Test
    public void shouldReturnScheduleTemplate_whenFindTemplateById() {
        Long id = 100500L;
        ScheduleTemplate template = ScheduleTemplate.builder().withId(id).build();

        when(templateRepository.findById(id))
                .thenReturn(Optional.of(template));

        assertThat(scheduleService.findTemplateById(id))
                .isEqualTo(Optional.of(template));
    }

    @Test
    public void shouldReturnEmptyOptional_whenFindTemplateById() {
        Long id = 100500L;

        when(templateRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThat(scheduleService.findTemplateById(id))
                .isEqualTo(Optional.empty());
    }

    @Test
    public void shouldInvoke_whenDeleteTemplateById() {
        Long id = 100500L;
        ScheduleTemplate template = ScheduleTemplate.builder().withId(id).build();

        scheduleService.deleteTemplateById(id);

        verify(templateRepository, times(1)).deleteById(id);
    }

    @Test
    public void shouldReturnList_whenGetTemplatesByOwner() {
        User owner = new User();
        List<ScheduleTemplate> templates =
                Collections.singletonList(ScheduleTemplate.builder().withOwner(owner).build());

        when(templateRepository.findAllByOwner(owner))
                .thenReturn(templates);

        List<ScheduleTemplate> actual = scheduleService.getTemplatesByOwner(owner);

        assertThat(actual)
                .containsExactlyElementsOf(templates);
    }


    @Test
    public void shouldReturnList_whenCreateNextWeekEventsBasedOnStandardSchema() {
        User owner = new User("owner@email.com", "pass");
        LocalDate date = scheduleService.getNextWeekFirstDay();
        List<ScheduleTemplate> templates = populateEventTemplates(owner);
        List<ScheduleEvent> expectedEvents = createEventsOnTemplates(templates, date);

        Mockito.when(templateRepository.findAllByOwner(owner))
                .thenReturn(templates);
        Mockito.when(eventRepository.saveAll(argThat((ArgumentMatcher<List<ScheduleEvent>>) events -> {
            for (int i = 0; i < 10; i++) {
                Assertions.assertThat(events.get(i))
                        .isEqualToIgnoringGivenFields(
                                expectedEvents.get(i), "createdAt", "modifiedAt");
            }
            return true;
        }))).thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.createNextWeekEventsForOwner(owner);

        assertThat(actualEvents)
                .isNotEmpty()
                .hasSize(10); // 2 events for every of 5 days
    }

    @Test
    public void shouldReturnList_whenCreateEventsBasedOnStandardSchemaWithHoliday() {
        User owner = new User("owner@email.com", "pass");
        LocalDate date = scheduleService.getNextWeekFirstDay();
        List<ScheduleTemplate> templates = populateEventTemplates(owner);
        List<ScheduleEvent> expectedEvents = createEventsOnTemplates(templates, date);
        expectedEvents.remove(1);
        expectedEvents.remove(0);
        List<ScheduleHoliday> holidays = Collections.singletonList(new ScheduleHoliday(date, date.plusDays(5)));

        Mockito.when(templateRepository.findAllByOwner(owner))
                .thenReturn(templates);
        Mockito.when(holidayRepository.findAllByHolidayDateBetween(date, date.plusDays(4)))
                .thenReturn(holidays);
        Mockito.when(eventRepository.saveAll(argThat((ArgumentMatcher<List<ScheduleEvent>>) events -> {
            for (int i = 0; i < 8; i++) {
                Assertions.assertThat(events.get(i))
                        .isEqualToIgnoringGivenFields(
                                expectedEvents.get(i), "createdAt", "modifiedAt");
            }
            return true;
        }))).thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.createNextWeekEventsForOwner(owner);

        assertThat(actualEvents)
                .isNotEmpty()
                .hasSize(8); // 2 events for every of 4 days
    }

    @Test
    public void shouldReturnList_whenCreateEventsBasedOnStandardSchemaWithSubstitutionDayThisWeek() {
        User owner = new User("owner@email.com", "pass");
        LocalDate date = scheduleService.getNextWeekFirstDay();
        List<ScheduleTemplate> templates = populateEventTemplates(owner);
        List<ScheduleEvent> expectedEvents = createEventsOnTemplates(templates, date);
        int daysToMoveHoliday = 5;
        List<ScheduleHoliday> holidays =
                Collections.singletonList(new ScheduleHoliday(date, date.plusDays(daysToMoveHoliday)));
        for (int i = 0; i < 2; i++) { //change dates of 2 events on Monday to the date of Saturday
            expectedEvents.get(0).setStartOfEvent(LocalDateTime.of(
                    date.plusDays(daysToMoveHoliday),
                    expectedEvents.get(0).getStartOfEvent().toLocalTime()));
            expectedEvents.get(0).setEndOfEvent(LocalDateTime.of(
                    date.plusDays(daysToMoveHoliday),
                    expectedEvents.get(0).getEndOfEvent().toLocalTime()));
            expectedEvents.add(expectedEvents.remove(0));
        }
        Mockito.when(templateRepository.findAllByOwner(owner))
                .thenReturn(templates);
        Mockito.when(holidayRepository.findAllByHolidayDateBetween(date, date.plusDays(4)))
                .thenReturn(holidays);
        Mockito.when(holidayRepository.findAllBySubstitutionDateBetween(date.plusDays(5), date.plusDays(6)))
                .thenReturn(holidays);
        Mockito.when(eventRepository.saveAll(argThat((ArgumentMatcher<List<ScheduleEvent>>) events -> {
            for (int i = 0; i < 8; i++) {
                Assertions.assertThat(events.get(i))
                        .isEqualToIgnoringGivenFields(
                                expectedEvents.get(i), "createdAt", "modifiedAt");
            }
            return true;
        }))).thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.createNextWeekEventsForOwner(owner);

        assertThat(actualEvents)
                .isNotEmpty()
                .hasSize(10); // 2 events for every one of those 5 days
    }

    @Test
    public void shouldReturnList_whenCreateEventsBasedOnStandardSchemaWithSubstitutionDayPreviousWeek() {
        User owner = new User("owner@email.com", "pass");
        LocalDate date = scheduleService.getNextWeekFirstDay();
        List<ScheduleTemplate> templates = populateEventTemplates(owner);
        List<ScheduleEvent> expectedEvents = createEventsOnTemplates(templates, date);
        int daysMondayToSaturday = 5; // Monday + 5 days => Saturday
        List<ScheduleHoliday> holidays = Collections.singletonList(new ScheduleHoliday(
                date.minusDays(7),                      // Previous Monday holiday's working day is moved to
                date.plusDays(daysMondayToSaturday)));  // this Saturday
        expectedEvents.addAll(createEventsOnTemplates(  // expecting for previous Monday events added to this of Saturday
                Arrays.asList(templates.get(0), templates.get(1)),
                date.plusDays(daysMondayToSaturday)));

        Mockito.when(templateRepository.findAllByOwner(owner))
                .thenReturn(templates);
        Mockito.when(holidayRepository.findAllByHolidayDateBetween(date, date.plusDays(4)))                     //Mo..Fr
                .thenReturn(holidays);
        Mockito.when(holidayRepository.findAllBySubstitutionDateBetween(date.plusDays(5), date.plusDays(6)))    //Sa..Su
                .thenReturn(holidays);
        Mockito.when(eventRepository.saveAll(argThat((ArgumentMatcher<List<ScheduleEvent>>) events -> {
            for (int i = 0; i < 12; i++) {
                Assertions.assertThat(events.get(i))
                        .isEqualToIgnoringGivenFields(
                                expectedEvents.get(i), "createdAt", "modifiedAt");
            }
            return true;
        }))).thenReturn(expectedEvents);

        List<ScheduleEvent> actualEvents = scheduleService.createNextWeekEventsForOwner(owner);

        assertThat(actualEvents)
                .isNotEmpty()
                .hasSize(12); // 2 events for every one of 6 working days this week
    }

    @Test
    public void shouldReturnTemplate_whenCreateEventsTemplate() {
        User owner = new User("owner@email.com", "pass");
        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        Role allowedRole = new Role(RolesFactory.ROLE_ALLOWED_CREATE_EVENT);
        eventType.addOwner(allowedRole);
        owner.addRole(allowedRole);
        DayOfWeek day = DayOfWeek.MONDAY;
        int minutes = 55;

        assertThat(eventType.getOwners())
                .contains(allowedRole);

        ScheduleTemplate expectedTemplate = ScheduleTemplate.builder()
                .withTitle(eventType.getName())
                .withDescription(day.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()) + ", 9:00")
                .withLocation(null)
                .withOwner(owner)
                .withType(eventType)
                .withDayOfWeek(day)
                .withStartOfEvent(LocalTime.of(9, 0))
                .withEndOfEvent(LocalTime.of(9, minutes))
                .build();

        ScheduleEventDTO eventDTO = ScheduleEventDTO.ScheduleEventDTOBuilder.aScheduleEventDTO()
                .withTitle(expectedTemplate.getTitle())
                .withDescription(expectedTemplate.getDescription())
                .withEventType(eventType.getName())
                .withStartTime(expectedTemplate.getStartOfEvent())
                .build();

        when(eventTypeRepo.findByName(eventType.getName()))
                .thenReturn(eventType);
        when(templateRepository.save(argThat((ArgumentMatcher<ScheduleTemplate>) template -> {
            Assertions.assertThat(template)
                    .isEqualToIgnoringGivenFields(expectedTemplate, "createdAt");
            return true;
        }))).thenReturn(expectedTemplate);

        ScheduleTemplate actualTemplate = scheduleService.createEventsTemplate(owner, eventDTO, day, minutes);

        assertThat(actualTemplate)
                .isNotNull();
    }

    private List<ScheduleEvent> createEventsOnTemplates(List<ScheduleTemplate> templates,
                                                        LocalDate date) {
        List<ScheduleEvent> events = new ArrayList<>();
        templates.forEach(template -> events.add(ScheduleEvent.builder()
                    .withTitle(template.getTitle())
                    .withDescription(template.getDescription())
                    .withLocation(template.getLocation())
                    .withOwner(template.getOwner())
                    .withType(template.getType())
                    .withStartDateTime(LocalDateTime.of(
                            date.plusDays(template.getDayOfWeek().getValue() - 1),
                            template.getStartOfEvent()))
                    .withEndDateTime(LocalDateTime.of(
                            date.plusDays(template.getDayOfWeek().getValue() - 1),
                            template.getEndOfEvent()))
                    .withOpenStatus(true)
                    .withCancelledStatus(false)
                    .withAccomplishedStatus(false)
                    .build()));

        return events;
    }

    private List<ScheduleTemplate> populateEventTemplates(User owner) {
        ScheduleEventType eventType = ScheduleEventTypeFactory.createScheduleEventType();
        List<ScheduleTemplate> templates = new ArrayList<>();
        List<DayOfWeek> daysList = Arrays.asList(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY);
        for (DayOfWeek dayOfWeek : daysList) {
            for (int i = 9; i < 11; i++) {
                templates.add(
                        ScheduleTemplate.builder()
                                .withTitle(eventType.getName())
                                .withDescription(dayOfWeek.getDisplayName(
                                        TextStyle.SHORT,
                                        Locale.getDefault()) + ", " + i + ":00")
                                .withLocation(null)
                                .withOwner(owner)
                                .withType(eventType)
                                .withDayOfWeek(dayOfWeek)
                                .withStartOfEvent(LocalTime.of(i, 0))
                                .withEndOfEvent(LocalTime.of(i, 55))
                                .build());
            }
        }

        return templates;
    }
}
