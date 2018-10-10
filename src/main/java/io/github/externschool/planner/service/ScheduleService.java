package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventDTO;
import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 * @author Benkoff (mailto.benkoff@gmal.com)
 */
public interface ScheduleService {
    ScheduleEvent createEvent(User owner, ScheduleEventReq eventReq);

    ScheduleEvent createEventWithDuration(User owner, ScheduleEventDTO eventDTO, int minutes);

    ScheduleEvent addParticipant(User participant, ScheduleEvent event);

    LocalDate getCurrentWeekFirstDay();

    LocalDate getNextWeekFirstDay();

    List<LocalDate> getWeekStartingFirstDay(LocalDate firstDay);

    List<ScheduleEvent> getEventsByOwnerAndDate(User owner, LocalDate date);
}
