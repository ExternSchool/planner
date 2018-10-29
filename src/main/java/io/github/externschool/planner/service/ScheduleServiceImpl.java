package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.UserCannotCreateEventException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.externschool.planner.util.Constants.LOCALE;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 * @author Benkoff (mailto.benkoff@gmail.com)
 */
@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleEventRepository eventRepo;
    private final ScheduleEventTypeRepository eventTypeRepo;

    @Autowired
    public ScheduleServiceImpl(final ScheduleEventRepository eventRepo,
                               final ScheduleEventTypeRepository eventTypeRepo) {
        this.eventRepo = eventRepo;
        this.eventTypeRepo = eventTypeRepo;
    }

    @Override
    public ScheduleEvent createEvent(User owner, ScheduleEventReq eventReq) {

        //TODO need case when event with this type is not found
        ScheduleEventType type = this.eventTypeRepo.findByName(eventReq.getEventType());

        canUserCreateEventForType(owner, type);

        ScheduleEvent newEvent = ScheduleEvent.builder()
                .withTitle(eventReq.getTitle())
                .withDescription(eventReq.getDescription())
                .withLocation(eventReq.getLocation())
                .withStartDateTime(eventReq.getStartOfEvent())
                .withEndDateTime(eventReq.getEndOfEvent())
                .withOwner(owner)
                .withType(type)
                .build();

        return this.eventRepo.save(newEvent);
    }

    @Override
    public ScheduleEvent createEventWithDuration(final User owner, final ScheduleEventDTO eventDTO, final int minutes) {

        // TODO need case when event with this type is not found
        ScheduleEventType type = this.eventTypeRepo.findByName(eventDTO.getEventType());
        canUserCreateEventForType(owner, type);

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

        return this.eventRepo.save(newEvent);
    }

    @Override
    public ScheduleEvent addParticipant(final User participant, ScheduleEvent event) {

        // TODO check for user rights to participate in this event
        // TODO add number of users by type verification
        if (participant != null && event != null && event.isOpen()) {
            Set<User> participants = new HashSet<>(event.getParticipants());
            participants.add(participant);
            event.setParticipants(participants);
        }

        return this.eventRepo.save(event);
    }

    @Override
    public ScheduleEvent getEventById(long id) {
        return eventRepo.getOne(id);
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

    private void canUserCreateEventForType(User user, ScheduleEventType type) {

        for (Role role : user.getRoles()) {
            if (type.getCreators().contains(role)) {
                return;
            }
        }

        throw new UserCannotCreateEventException(
                String.format("The user %s is not allowed to create this type of event", user.getEmail())
        );
    }

    @Override
    public List<ScheduleEvent> getEventsByOwnerAndDate(final User owner, final LocalDate date) {
        return eventRepo.findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(owner,
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX));
    }

    @Override
    public List<ScheduleEvent> getEventsByOwner(User owner) {
        return eventRepo.findAllByOwner(owner);
    }

    @Override
    public void deleteEvent(long id) {
        eventRepo.deleteById(id);
    }
}
