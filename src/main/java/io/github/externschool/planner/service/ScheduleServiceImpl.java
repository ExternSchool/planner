package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.exceptions.UserCannotCreateEventException;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

import static io.github.externschool.planner.util.Constants.LOCALE;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 * @author Benkoff (mailto.benkoff@gmal.com)
 */
@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleEventRepository eventRepo;
    private final ScheduleEventTypeRepository eventTypeRepo;

    @Autowired
    public ScheduleServiceImpl(final ScheduleEventRepository eventRepo,
                               final ScheduleEventTypeRepository eventTypeRepo) {
        this.eventRepo = eventRepo;
        this.eventTypeRepo = eventTypeRepo;
    }

    @Override
    public ScheduleEvent createEvent(User user, ScheduleEventReq eventReq) {

        //TODO need case when event with this type is not found
        ScheduleEventType type = this.eventTypeRepo.findByName(eventReq.getEventType());

        canUserCreateEventForType(user, type);

        ScheduleEvent newEvent = ScheduleEvent.builder()
                .withTitle(eventReq.getTitle())
                .withDescription(eventReq.getDescription())
                .withLocation(eventReq.getLocation())
                .withStartDateTime(eventReq.getStartOfEvent())
                .withEndDateTime(eventReq.getEndOfEvent())
                .withOwner(user)
                .withType(type)
                .build();

        return this.eventRepo.save(newEvent);
    }

    @Override
    public LocalDate getCurrentWeekFirstDay() {
        LocalDate now = LocalDate.now();
        TemporalField fieldISO = WeekFields.of(LOCALE).dayOfWeek();

        return now.with(fieldISO, 1);
    }

    @Override
    public List<LocalDate> getWeekStartingFirstDay(final LocalDate firstDay) {
        ArrayList<LocalDate> week = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            week.add(firstDay.plus(Period.of(0, 0, i)));
        }

        return week;
    }

    @Override
    public LocalDate getNextWeekFirstDay() {
        return getCurrentWeekFirstDay().plus(Period.of(0, 0, 7));
    }

    private void canUserCreateEventForType(User user, ScheduleEventType type) {

        for (Role role : user.getRoles()) {
            if (type.getCreators().contains(role)) {
                return;
            }
        }

        throw new UserCannotCreateEventException(
                String.format("The user %s is not allowed to create this type of event", user.getEmail())
        );
    }
}
