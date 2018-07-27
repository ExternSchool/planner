package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public interface ScheduleService {
    ScheduleEvent createEvent(User user, ScheduleEventReq eventReq);
}
