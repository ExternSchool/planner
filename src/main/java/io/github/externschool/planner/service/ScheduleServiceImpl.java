package io.github.externschool.planner.service;

import io.github.externschool.planner.dto.ScheduleEventReq;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
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
}
