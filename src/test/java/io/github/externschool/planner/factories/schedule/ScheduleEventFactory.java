package io.github.externschool.planner.factories.schedule;

import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.factories.RolesFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static io.github.externschool.planner.factories.UserFactory.createUser;
import static io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory.SCHEDULE_EVENT_TYPE_NAME;
import static io.github.externschool.planner.factories.schedule.ScheduleEventTypeFactory.createScheduleEventType;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class ScheduleEventFactory {
    private static final Long EVENT_ID = 1L;
    private static final String EVENT_TITLE = "TestEvent";
    private static final String EVENT_DESCRIPTION = "TestDescription";
    private static final String EVENT_LOCATION = "TestLocation";
    private static final LocalDate EVENT_DATE = LocalDate.of(2018, 6, 7);
    private static final LocalTime EVENT_START_TIME = LocalTime.of(8, 11);
    private static final LocalTime EVENT_END_TIME = LocalTime.of(10, 11);

    private ScheduleEventFactory() {}

    public static ScheduleEvent createNewScheduleEventWithoutParticipants() {
        User owner = createUser();
        owner.addRole(RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT));
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

    public static ScheduleEventReq createScheduleEventReq() {
        return new ScheduleEventReq(
                EVENT_TITLE,
                EVENT_DESCRIPTION,
                EVENT_LOCATION,
                LocalDateTime.of(EVENT_DATE, EVENT_START_TIME),
                LocalDateTime.of(EVENT_DATE, EVENT_END_TIME),
                SCHEDULE_EVENT_TYPE_NAME
        );
    }
}