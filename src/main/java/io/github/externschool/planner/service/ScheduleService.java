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

    List<ScheduleEvent> getEventsByOwnerStartingBetweenDates(User owner, LocalDate firstDate, LocalDate lastDate);

    List<ScheduleEvent> getEventsByType(ScheduleEventType type);

    void cancelEventByIdAndSave(long id);

    void findEventByIdSetOpenAndSave(long id, boolean state);

    void deleteEventById(long id);

    void deleteEventsAfterMailingToParticipants(List<ScheduleEvent> events);

    ScheduleEvent addOwner(User owner, ScheduleEvent event);

    void removeOwner(ScheduleEvent event);

    Optional<Participant> addParticipant(User user, ScheduleEvent event);

    Optional<Participant> findParticipantByUserAndEvent(User user, ScheduleEvent event);

    List<Participant> getParticipantsByUser(User user);

    boolean removeParticipant(Participant participant);

    LocalDate getCurrentWeekFirstDay();

    LocalDate getNextWeekFirstDay();

    List<LocalDate> getWeekStartingFirstDay(LocalDate firstDay);
}
