package io.github.externschool.planner.factories.schedule;

import io.github.externschool.planner.entity.schedule.ScheduleEventType;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class ScheduleEventTypeFactory {
    private ScheduleEventTypeFactory() {
    }

    public static final Long SCHEDULE_EVENT_TYPE_ID = 2L;
    public static final String SCHEDULE_EVENT_TYPE_NAME = "TestEventType";
    public static final Integer SCHEDULE_EVENT_TYPE_COUNT_OF_PARTICIPANT = 1;

    public static ScheduleEventType createScheduleEventType() {
        ScheduleEventType eventType = new ScheduleEventType(SCHEDULE_EVENT_TYPE_NAME, SCHEDULE_EVENT_TYPE_COUNT_OF_PARTICIPANT);
        eventType.setId(SCHEDULE_EVENT_TYPE_ID);
        return eventType;
    }
}
