package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Service
@Transactional
public class ScheduleEventTypeServiceImpl implements ScheduleEventTypeService {

    private final ScheduleEventTypeRepository eventTypeRepo;

    @Autowired
    public ScheduleEventTypeServiceImpl(final ScheduleEventTypeRepository eventTypeRepo) {
        this.eventTypeRepo = eventTypeRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleEventType> loadEventTypes() {
        return this.eventTypeRepo.findAll();
    }
}
