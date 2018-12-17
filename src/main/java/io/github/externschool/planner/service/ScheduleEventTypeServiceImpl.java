package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.repository.schedule.ScheduleEventTypeRepository;
import io.github.externschool.planner.util.CollatorHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    //TODO refactor to meet conversionService.convert(...)
    @Override
    public ScheduleEventType saveOrUpdateEventType(final ScheduleEventType eventType) {
        ScheduleEventType eventTypeToSave = Optional.ofNullable(eventTypeRepository.findByName(eventType.getName()))
                .map(storedEventType -> {
                    storedEventType.setName(eventType.getName());
                    storedEventType.setAmountOfParticipants(eventType.getAmountOfParticipants());
                    storedEventType.setDurationInMinutes(eventType.getDurationInMinutes());
                    new ArrayList<>(storedEventType.getOwners()).forEach(storedEventType::removeOwner);
                    new ArrayList<>(storedEventType.getParticipants()).forEach(storedEventType::removeParticipant);
                    eventType.getOwners().forEach(storedEventType::addOwner);
                    eventType.getParticipants().forEach(storedEventType::addParticipant);

                    return storedEventType;
                })
                .orElse(eventType);

        return eventTypeRepository.save(eventTypeToSave);
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
