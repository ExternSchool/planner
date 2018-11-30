package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.UserCannotHandleEventException;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.repository.schedule.ParticipantRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.LOCALE;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 * @author Benkoff (mailto.benkoff@gmail.com)
 */
@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleEventRepository eventRepository;
    private final ScheduleEventTypeRepository eventTypeRepo;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    @Autowired
    public ScheduleServiceImpl(final ScheduleEventRepository eventRepository,
                               final ScheduleEventTypeRepository eventTypeRepo,
                               final UserRepository userRepository,
                               final ParticipantRepository participantRepository) {
        this.eventRepository = eventRepository;
        this.eventTypeRepo = eventTypeRepo;
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * @deprecated
     * Use createEventWithDuration(final User owner, final ScheduleEventDTO eventDTO, final int minutes)
     * @return ScheduleEvent
     */
    @Deprecated
    @Override
    public ScheduleEvent createEvent(User owner, ScheduleEventReq eventReq) {

        //TODO need case when event with this type is not found
        ScheduleEventType type = this.eventTypeRepo.findByName(eventReq.getEventType());

        canUserHandleEventForType(owner, type);

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

        // TODO need case when event with this type is not found
        ScheduleEventType type = this.eventTypeRepo.findByName(eventDTO.getEventType());
        canUserHandleEventForType(owner, type);

        ScheduleEvent newEvent = ScheduleEvent.builder()
                .withTitle(eventDTO.getTitle())
                .withDescription(eventDTO.getDescription())
                .withStartDateTime(LocalDateTime.of(eventDTO.getDate(), eventDTO.getStartTime()))
                .withEndDateTime(LocalDateTime.of(eventDTO.getDate(),
                        eventDTO.getStartTime().plus(Duration.of(minutes, ChronoUnit.MINUTES))))
                .withOwner(owner)
                .withOpenStatus(eventDTO.getOpen())
                .withType(type)
                .build();
        eventRepository.save(newEvent);
        owner.addOwnEvent(newEvent);

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
    public List<ScheduleEvent> getActualEventsByOwnerAndDate(final User owner, final LocalDate date) {
        return eventRepository.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(
                owner,
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)).stream()
                .filter(event -> !event.isCancelled())
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleEvent> getEventsByOwner(User owner) {
        return eventRepository.findAllByOwner(owner);
    }

    @Override
    public void cancelEventById(long id) {
        eventRepository.findById(id).ifPresent(event -> {
            event.setCancelled(true);
            event.setOpen(false);
            event.setModifiedAt(LocalDateTime.now());

            // TODO move here cancelling notification from Teacher Controller
        });
    }

    @Override
    public void deleteEventById(long id) {
        eventRepository.findById(id).ifPresent(event -> {
            event.getParticipants().forEach(this::removeParticipant);
            Optional.ofNullable(event.getOwner()).ifPresent(owner -> {
                removeOwner(owner, event);
                eventRepository.save(event);
            });

            eventRepository.deleteById(id);
        });
    }

    @Override
    public ScheduleEvent addOwner(final User owner, final ScheduleEvent event) {
        if (owner != null && event != null && event.getOwner() == null) {
            canUserHandleEventForType(owner, event.getType());

            owner.addOwnEvent(event);
            eventRepository.save(event);
        }
        return event;
    }

    @Override
    public void removeOwner(final User owner, final ScheduleEvent event) {
        if (owner != null && event != null && event.getOwner() == owner) {
            owner.removeOwnEvent(event);
            eventRepository.save(event);
        }
    }

    @Override
    public ScheduleEvent addParticipant(User user, ScheduleEvent event) {

        // TODO check for user rights to participate in this event
        // TODO set number of users by event type
        if (user != null && event != null && event.isOpen()) {
            Participant participant = new Participant(user, event);
            user.addParticipant(participant);
            event.addParticipant(participant);
            event.setModifiedAt(LocalDateTime.now());

            participantRepository.save(participant);
            eventRepository.save(event);
        }

        return event;
    }

    @Transactional
    @Override
    public void removeParticipant(final Participant participant) {
        participantRepository.findById(participant.getId()).ifPresent(p -> {
            Optional.ofNullable(participant.getUser()).ifPresent(user -> {
                user.removeParticipant(participant);
                participantRepository.save(participant);
            });
            Optional.ofNullable(participant.getEvent()).ifPresent(event -> {
                event.removeParticipant(participant);
                participantRepository.save(participant);
            });
            participantRepository.delete(participant);
        });
    }

    @Override
    public LocalDate getCurrentWeekFirstDay() {
        LocalDate now = LocalDate.now();
        TemporalField fieldISO = WeekFields.of(LOCALE).dayOfWeek();

        return now.with(fieldISO, 1);
    }

    @Override
    public List<LocalDate> getWeekStartingFirstDay(final LocalDate firstDay) {
        ArrayList<LocalDate> week = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            week.add(firstDay.plus(Period.of(0, 0, i)));
        }

        return week;
    }

    @Override
    public LocalDate getNextWeekFirstDay() {
        return getCurrentWeekFirstDay().plus(Period.of(0, 0, 7));
    }

    private void canUserHandleEventForType(User user, ScheduleEventType type) {

        if (user != null && type != null) {
            for (Role role : user.getRoles()) {
                if (type.getCreators().contains(role)) {
                    return;
                }
            }
        }

        throw new UserCannotHandleEventException(
                String.format("The user %s is not allowed to create or own %s type of event",
                        user != null ? user.getEmail() : "NULL",
                        type != null ? type.getName() : "NULL"));
    }
}
