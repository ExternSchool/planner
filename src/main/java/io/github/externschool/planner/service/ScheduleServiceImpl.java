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
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.schedule.ParticipantRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import io.github.externschool.planner.repository.schedule.ScheduleHolidayRepository;
import io.github.externschool.planner.repository.schedule.ScheduleTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.LOCALE;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleEventRepository eventRepository;
    private final ScheduleEventTypeRepository eventTypeRepository;
    private final ParticipantRepository participantRepository;
    private final EmailService emailService;
    private final ScheduleHolidayRepository holidayRepository;
    private final ScheduleTemplateRepository templateRepository;
    private final UserRepository userRepository;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Autowired
    public ScheduleServiceImpl(final ScheduleEventRepository eventRepository,
                               final ScheduleEventTypeRepository eventTypeRepository,
                               final ParticipantRepository participantRepository,
                               final EmailService emailService,
                               final ScheduleHolidayRepository holidayRepository,
                               final ScheduleTemplateRepository templateRepository,
                               final UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.participantRepository = participantRepository;
        this.emailService = emailService;
        this.holidayRepository = holidayRepository;
        this.templateRepository = templateRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates and saves a new event
     *
     * @return ScheduleEvent
     * @deprecated Replaced by {@link #createEventWithDuration(User owner, ScheduleEventDTO eventDTO, int minutes)}
     */
    @Deprecated
    @Override
    public ScheduleEvent createEvent(User owner, ScheduleEventReq eventReq) {
        ScheduleEventType type = this.eventTypeRepository.findByName(eventReq.getEventType());

        canUserOwnAnEventForType(owner, type);

        ScheduleEvent newEvent = ScheduleEvent.builder()
                .withTitle(eventReq.getTitle())
                .withDescription(eventReq.getDescription())
                .withLocation(eventReq.getLocation())
                .withStartDateTime(eventReq.getStartOfEvent())
                .withEndDateTime(eventReq.getEndOfEvent())
                .withOwner(owner)
                .withType(type)
                .build();

        return this.eventRepository.save(newEvent);
    }

    @Override
    public ScheduleEvent createEventWithDuration(final User owner, final ScheduleEventDTO eventDTO, final int minutes) {
        ScheduleEventType type = eventTypeRepository.findByName(eventDTO.getEventType());
        canUserOwnAnEventForType(owner, type);

        ScheduleEvent newEvent = ScheduleEvent.builder()
                .withTitle(eventDTO.getTitle())
                .withDescription(eventDTO.getDescription())
                .withStartDateTime(LocalDateTime.of(eventDTO.getDate(), eventDTO.getStartTime()))
                .withEndDateTime(LocalDateTime.of(eventDTO.getDate(),
                        eventDTO.getStartTime().plus(Duration.of(minutes, ChronoUnit.MINUTES))))
                .withOwner(owner)
                .withType(type)
                .withOpenStatus(eventDTO.getOpen() != null ? eventDTO.getOpen() : true)
                .withCancelledStatus(false)
                .withAccomplishedStatus(false)
                .build();
        owner.addOwnEvent(newEvent);
        saveEvent(newEvent);
        userRepository.save(owner);

        return newEvent;
    }

    @Transactional(readOnly = true)
    @Override
    public ScheduleEvent getEventById(long id) {
        return eventRepository.getOne(id);
    }

    @Override
    public ScheduleEvent saveEvent(final ScheduleEvent event) {
        return eventRepository.save(event);
    }

    @Override
    public List<ScheduleEvent> getNonCancelledEventsByOwnerAndDate(final User owner, final LocalDate date) {
        return eventRepository.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(
                owner,
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)).stream()
                .filter(event -> !event.isCancelled())
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleEvent> getEventsByOwnerStartingBetweenDates(final User owner,
                                                                    final LocalDate firstDate,
                                                                    final LocalDate lastDate) {
        return eventRepository.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(
                owner,
                firstDate.atStartOfDay(),
                lastDate.atTime(LocalTime.MAX));
    }

    @Override
    public List<ScheduleEvent> getEventsByOwner(User owner) {
        return eventRepository.findAllByOwner(owner);
    }

    @Override
    public List<ScheduleEvent> getEventsByType(final ScheduleEventType type) {
        ScheduleEventType eventType = eventTypeRepository.findByName(type.getName());

        return eventRepository.findAllByType(eventType);
    }

    @Override
    public void deleteEventById(final long id) {
        eventRepository.findById(id).ifPresent(event -> {
            event.getParticipants().forEach(this::removeParticipant);
            Optional.ofNullable(event.getOwner()).ifPresent(owner -> {
                removeOwner(event);
                event.setType(null);
                eventRepository.save(event);
            });

            eventRepository.deleteById(id);
        });
    }

    @Override
    public Optional<ScheduleEvent> findEventByIdSetOpenByStateAndSave(final long id, final boolean state) {
        eventRepository.findById(id).ifPresent(event -> {
            event.setOpen(state);
            eventRepository.save(event);
        });

        return eventRepository.findById(id);
    }

    @Override
    public Optional<ScheduleEvent> findEventByIdSetCancelledNotOpenAndSave(final long id) {
        eventRepository.findById(id).ifPresent(event -> {
            event.setCancelled(true);
            event.setOpen(false);
            eventRepository.save(event);
        });

        return eventRepository.findById(id);
    }

    @Override
    public void cancelEventsAndMailToParticipants(final List<ScheduleEvent> events) {

        events.forEach(event -> {
            executor.execute(() -> emailService.sendCancelEventMail(event));

            findEventByIdSetCancelledNotOpenAndSave(event.getId());
        });
    }

    @Override
    public ScheduleEvent addOwner(final User owner, final ScheduleEvent event) {
        if (owner != null && event != null && event.getOwner() == null) {
            canUserOwnAnEventForType(owner, event.getType());

            owner.addOwnEvent(event);
            eventRepository.save(event);
        }
        return event;
    }

    @Override
    public void removeOwner(final ScheduleEvent event) {
        if (event != null && event.getOwner() != null) {
            event.getOwner().removeOwnEvent(event);
            eventRepository.save(event);
        }
    }

    @Override
    public Optional<Participant> findParticipantByUserAndEvent(final User user, final ScheduleEvent event) {
        return participantRepository.findParticipantByUserAndEvent(user, event);
    }

    @Override
    public List<Participant> getParticipantsByUser(final User user) {
        return participantRepository.getAllByUser(user);
    }

    @Override
    public Optional<Participant> addParticipant(final User user, ScheduleEvent event)
            throws UserCanNotHandleEventException {
        Optional<Participant> optionalParticipant = Optional.empty();

        if (user != null && event != null && event.isOpen()) {
            canUserParticipateInEventForType(user, event.getType());
            int maximum = event.getType().getAmountOfParticipants();
            Set participants = event.getParticipants();
            if (participants.size() < maximum) {
                Participant participant = new Participant(user, event);
                if (participants.size() >= maximum) {
                    event.setOpen(false);
                }
                optionalParticipant = Optional.ofNullable(participantRepository.save(participant));
                eventRepository.save(event);
            }
        }

        return optionalParticipant;
    }

    @Override
    public void removeParticipant(final Participant participant) {
        if (participant != null) {
            Optional.ofNullable(participant.getUser()).ifPresent(user -> {
                user.removeParticipant(participant);
                userRepository.save(user);
                participantRepository.save(participant);
            });
            Optional.ofNullable(participant.getEvent()).ifPresent(event -> {
                event.removeParticipant(participant);
                eventRepository.save(event);
                participantRepository.save(participant);
            });
            participantRepository.delete(participant);
        }
    }

    @Override
    public LocalDate getCurrentWeekFirstDay() {
        LocalDate now = LocalDate.now();
        if (now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY) {
            now = now.plusDays(2L);
        }
        TemporalField fieldISO = WeekFields.of(LOCALE).dayOfWeek();

        return now.with(fieldISO, 1);
    }

    @Override
    public List<LocalDate> getWeekStartingFirstDay(final LocalDate firstDay) {
        ArrayList<LocalDate> week = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            week.add(firstDay.plusDays(i));
        }

        return week;
    }

    @Override
    public LocalDate getNextWeekFirstDay() {
        return getCurrentWeekFirstDay().plusWeeks(1);
    }

    private void canUserOwnAnEventForType(final User user, final ScheduleEventType type) {
        if (user != null && type != null) {
            for (Role role : user.getRoles()) {
                if (type.getOwners().contains(role)) {
                    return;
                }
            }
        }
        throw new UserCanNotHandleEventException(
                String.format("The user %s is not allowed to create or own %s type of event",
                        user != null ? user.getEmail() : "NULL",
                        type != null ? type.getName() : "NULL"));
    }

    @Override
    public ScheduleHoliday saveHoliday(final LocalDate holiday, final LocalDate substitutionDay) {
        ScheduleHoliday scheduleHoliday = new ScheduleHoliday(holiday, substitutionDay);

        return holidayRepository.save(scheduleHoliday);
    }

    @Override
    public Optional<ScheduleHoliday> findHolidayById(final Long id) {
        return holidayRepository.findById(id);
    }

    @Override
    public void deleteHolidayById(final Long id) {
        holidayRepository.deleteById(id);
    }

    @Override
    public List<ScheduleHoliday> getHolidaysBetweenDates(final LocalDate start, final LocalDate end) {
        return holidayRepository.findAllByHolidayDateBetween(start, end);
    }

    @Override
    public ScheduleTemplate saveTemplate(final ScheduleTemplate template) {
        return templateRepository.save(template);
    }

    @Override
    public Optional<ScheduleTemplate> findTemplateById(final Long id) {
        return templateRepository.findById(id);
    }

    @Override
    public void deleteTemplateById(final Long id) {
        templateRepository.deleteById(id);
    }

    @Override
    public List<ScheduleTemplate> getTemplatesByOwner(final User owner) {
        return templateRepository.findAllByOwner(owner);
    }

    @Override
    public List<ScheduleEvent> createCurrentWeekEventsForOwner(final User owner) {
        return eventRepository.saveAll(createEventsForOwnerByFirstDayOfWeek(owner, getCurrentWeekFirstDay()));
    }

    @Override
    public List<ScheduleEvent> createNextWeekEventsForOwner(final User owner) {
        return eventRepository.saveAll(createEventsForOwnerByFirstDayOfWeek(owner, getNextWeekFirstDay()));
    }

    @Override
    public ScheduleTemplate createEventsTemplate(final User owner,
                                                 final ScheduleEventDTO eventDTO,
                                                 final DayOfWeek day,
                                                 final int minutes) {
        ScheduleEventType type = eventTypeRepository.findByName(eventDTO.getEventType());
        canUserOwnAnEventForType(owner, type);

        ScheduleTemplate template = ScheduleTemplate.builder()
                .withTitle(eventDTO.getTitle())
                .withDescription(eventDTO.getDescription())
                .withDayOfWeek(day)
                .withStartOfEvent(eventDTO.getStartTime())
                .withEndOfEvent(eventDTO.getStartTime().plus(Duration.of(minutes, ChronoUnit.MINUTES)))
                .withCreatedAt(LocalDateTime.now())
                .withOwner(owner)
                .withType(type)
                .build();

        return templateRepository.save(template);
    }

    private List<ScheduleEvent> createEventsForOwnerByFirstDayOfWeek(final User owner,
                                                                     final LocalDate firstDay) {
        List<ScheduleEvent> newEvents = new ArrayList<>();
        List<ScheduleTemplate> templates = templateRepository.findAllByOwner(owner);
        Set<LocalDate> holidayDates = holidayRepository.findAllByHolidayDateBetween(
                firstDay,
                firstDay.plusDays(DayOfWeek.FRIDAY.getValue() - 1))
                    .stream()
                    .map(ScheduleHoliday::getHolidayDate)
                    .collect(Collectors.toSet());
        // Monday to Friday -- create usual working days schedule with templates
        List<DayOfWeek> daysList = Arrays.asList(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY);
        for (DayOfWeek dayOfWeek : daysList) {
            LocalDate date = firstDay.plusDays(dayOfWeek.getValue() - 1);
            if (!holidayDates.contains(date)) {
                // this is an ordinary working day, not a holiday, so add it
                templates.stream()
                        .filter(template -> template.getDayOfWeek().equals(dayOfWeek))
                        .map(template -> createEventForDate(template, date))
                        .forEach(newEvents::add);
            } // else when current day is a holiday, do not create events
        }
        // Saturday To Sunday -- add events for the days which are working days this week with holidays' templates
        List<ScheduleHoliday> substitutionDays = holidayRepository.findAllBySubstitutionDateBetween(
                firstDay.plusDays(DayOfWeek.SATURDAY.getValue() - 1),
                firstDay.plusDays(DayOfWeek.SUNDAY.getValue() - 1));
        for (ScheduleHoliday day : substitutionDays) {
            // this holiday is a substitution working day for another day so fill it with events of that holiday
            DayOfWeek holidayDayOfWeek = day.getHolidayDate().getDayOfWeek();
            templates.stream()
                    .filter(template -> template.getDayOfWeek().equals(holidayDayOfWeek))
                    .map(template -> createEventForDate(template, day.getSubstitutionDate()))
                    .forEach(newEvents::add);
        }

        return newEvents;
    }

    private ScheduleEvent createEventForDate(final ScheduleTemplate template, final LocalDate date) {
        return ScheduleEvent.builder()
                .withTitle(template.getTitle())
                .withDescription(template.getDescription())
                .withLocation(template.getLocation())
                .withOwner(template.getOwner())
                .withType(template.getType())
                .withStartDateTime(LocalDateTime.of(date, template.getStartOfEvent()))
                .withEndDateTime(LocalDateTime.of(date, template.getEndOfEvent()))
                .withOpenStatus(true)
                .withCancelledStatus(false)
                .withAccomplishedStatus(false)
                .build();
    }

    private void canUserParticipateInEventForType(final User user, final ScheduleEventType type) {
        if (user != null && type != null) {
            for (Role role : user.getRoles()) {
                if (type.getParticipants().contains(role)) {
                    return;
                }
            }
        }
        throw new UserCanNotHandleEventException(
                String.format("The user %s is not allowed to participate in %s type of event",
                        user != null ? user.getEmail() : "NULL",
                        type != null ? type.getName() : "NULL"));
    }
}
