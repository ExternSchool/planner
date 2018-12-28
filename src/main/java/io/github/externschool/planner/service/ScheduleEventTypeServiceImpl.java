package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import io.github.externschool.planner.util.CollatorHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleEventTypeServiceImpl implements ScheduleEventTypeService {
    private final ScheduleEventTypeRepository eventTypeRepository;

    @Autowired
    public ScheduleEventTypeServiceImpl(final ScheduleEventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public Optional<ScheduleEventType> getEventTypeById(final Long id) {
        return Optional.ofNullable(id).flatMap(eventTypeRepository::findById);
    }

    @Override
    public ScheduleEventType saveEventType(final ScheduleEventType eventType) {
        return eventTypeRepository.save(eventType);
    }

    @Override
    public void deleteEventType(final ScheduleEventType eventType) {
        eventTypeRepository.delete(eventType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleEventType> loadEventTypes() {
        return eventTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleEventType> getAllEventTypesSorted() {
        return eventTypeRepository.findAll().stream()
                .sorted(Comparator.comparing(ScheduleEventType::getName,
                        Comparator.nullsFirst(CollatorHolder.getUaCollator())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleEventType> getAllEventTypesByUserRoles(User user) {
        return eventTypeRepository.findAll().stream()
                .filter(eventType -> !Collections.disjoint(eventType.getOwners(), user.getRoles()))
                .sorted(Comparator.comparing(ScheduleEventType::getName,
                        Comparator.nullsFirst(CollatorHolder.getUaCollator())))
                .collect(Collectors.toList());
    }
}
