package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    ScheduleEvent createEvent(User owner, ScheduleEventReq eventReq);

    ScheduleEvent createEventWithDuration(User owner, ScheduleEventDTO eventDTO, int minutes);

    ScheduleEvent getEventById(long id);

    ScheduleEvent saveEvent(ScheduleEvent event);

    List<ScheduleEvent> getActualEventsByOwnerAndDate(User owner, LocalDate date);

    List<ScheduleEvent> getEventsByOwner(User owner);

    List<ScheduleEvent> getEventsByType(ScheduleEventType type);

    void cancelEventById(long id);

    void deleteEventById(long id);

    ScheduleEvent addOwner(User owner, ScheduleEvent event);

    void removeOwner(User owner, ScheduleEvent event);

    Optional<Participant> addParticipant(User user, ScheduleEvent event);

    Optional<Participant> getParticipantByUserAndEvent(User user, ScheduleEvent event);

    void removeParticipant(Participant participant);

    LocalDate getCurrentWeekFirstDay();

    LocalDate getNextWeekFirstDay();

    List<LocalDate> getWeekStartingFirstDay(LocalDate firstDay);
}
