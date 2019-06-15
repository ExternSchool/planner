package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;

import java.util.List;
import java.util.Optional;

public interface ScheduleEventTypeService {
    Optional<ScheduleEventType> getEventTypeById(Long id);

    ScheduleEventType saveEventType(ScheduleEventType eventType);

    void deleteEventType(ScheduleEventType eventType);

    List<ScheduleEventType> loadEventTypes();

    List<ScheduleEventType> getAllEventTypesSorted();

    List<ScheduleEventType> getAllEventTypesByUserRoles(User user);
}
