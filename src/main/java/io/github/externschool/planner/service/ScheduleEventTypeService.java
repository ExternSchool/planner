package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.schedule.ScheduleEventType;

import java.util.List;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public interface ScheduleEventTypeService {
    List<ScheduleEventType> loadEventTypes();
}
