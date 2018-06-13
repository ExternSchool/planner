package io.github.externschool.planner.factories.schedule;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static io.github.externschool.planner.factories.UserFactory.createUser;
import static io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory.createScheduleEventType;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class ScheduleEventFactory {
    private ScheduleEventFactory() {}

    public static final Long EVENT_ID = 1L;
    public static final String EVENT_TITLE = "TestEvent";
    public static final String EVENT_DESCRIPTION = "TestDescription";
    public static final String EVENT_LOCATION = "TestLocation";
    public static final LocalDate EVENT_DATE = LocalDate.of(2018, 6, 7);
    public static final LocalTime EVENT_START_TIME = LocalTime.of(8, 11);
    public static final LocalTime EVENT_END_TIME = LocalTime.of(10, 11);


    public static ScheduleEvent createNewScheduleEventWithoutParticipants() {
        User owner = createUser();
        ScheduleEventType type = createScheduleEventType();
        return ScheduleEvent
                .builder()
                .withId(EVENT_ID)
                .withTitle(EVENT_TITLE)
                .withDescription(EVENT_DESCRIPTION)
                .withLocation(EVENT_LOCATION)
                .withOwner(owner)
                .withType(type)
                .withStartDateTime(LocalDateTime.of(EVENT_DATE, EVENT_START_TIME))
                .withEndDateTime(LocalDateTime.of(EVENT_DATE, EVENT_END_TIME))
                .build();
    }
}