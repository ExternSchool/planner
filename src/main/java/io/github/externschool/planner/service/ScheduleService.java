package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.entity.schedule.ScheduleHoliday;
import io.github.externschool.planner.entity.schedule.ScheduleTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    // Events

    ScheduleEvent createEvent(User owner, ScheduleEventReq eventReq);

    ScheduleEvent createEventWithDuration(User owner, ScheduleEventDTO eventDTO, int minutes);

    ScheduleEvent getEventById(long id);

    ScheduleEvent saveEvent(ScheduleEvent event);

    List<ScheduleEvent> getNonCancelledEventsByOwnerAndDate(User owner, LocalDate date);

    List<ScheduleEvent> getEventsByOwner(User owner);

    List<ScheduleEvent> getEventsByOwnerStartingBetweenDates(User owner, LocalDate firstDate, LocalDate lastDate);

    List<ScheduleEvent> getEventsByType(ScheduleEventType type);

    void deleteEventById(long id);

    Optional<ScheduleEvent> findEventByIdSetOpenByStateAndSave(long id, boolean state);

    Optional<ScheduleEvent> findEventByIdSetCancelledNotOpenAndSave(long id);

    void cancelEventsAndMailToParticipants(List<ScheduleEvent> events);

    // Event Owners and Participants

    ScheduleEvent addOwner(User owner, ScheduleEvent event);

    void removeOwner(ScheduleEvent event);

    Optional<Participant> findParticipantByUserAndEvent(User user, ScheduleEvent event);

    List<Participant> getParticipantsByUser(User user);

    Optional<Participant> addParticipant(User user, ScheduleEvent event);

    Participant saveParticipant(Participant participant);

    void removeParticipant(Participant participant);

    // Calendar Days

    LocalDate getCurrentWeekFirstDay();

    LocalDate getNextWeekFirstDay();

    List<LocalDate> getWeekStartingFirstDay(LocalDate firstDay);

    List<ScheduleEvent> createNextWeekEventsForOwner(User owner);

    ScheduleHoliday saveHoliday(LocalDate holiday, LocalDate substitutionDay);

    Optional<ScheduleHoliday> findHolidayById(Long id);

    void deleteHolidayById(Long id);

    List<ScheduleHoliday> getHolidaysBetweenDates(LocalDate start, LocalDate end);

    // Event Templates

    ScheduleTemplate createTemplate(User owner, ScheduleEventDTO eventDTO, DayOfWeek dayOfWeek, int durationInMinutes);

    ScheduleTemplate saveTemplate(ScheduleTemplate template);

    Optional<ScheduleTemplate> findTemplateById(Long id);

    void deleteTemplateById(Long id);

    List<ScheduleTemplate> getTemplatesByOwner(User owner);

    List<ScheduleEvent> getDailyTemplateEventsByOwner(User owner);
}
