package io.github.externschool.planner.util;

import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@Transactional
public class ScheduledTasks {
    private final ScheduleService scheduleService;
    private final TeacherService teacherService;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    public ScheduledTasks(final ScheduleService scheduleService, final TeacherService teacherService) {
        this.scheduleService = scheduleService;
        this.teacherService = teacherService;
    }

    //TODO change to Saturday 1:01 a.m.
    @Scheduled(cron="0 1 1 * * *", zone="Europe/Kiev")
    public void doScheduledWork() {
        CompletableFuture<List<ScheduleEvent>> future =  recreateEventsForAllTeachers();
        future.thenApply(eventList -> {
            for (ScheduleEvent event : eventList) {
                log.debug("Add event: {} ", event.getId());
            }
            return null;
        });

        try {
            log.info("Next week events created with templates present: {}", future.get().size());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<List<ScheduleEvent>> recreateEventsForAllTeachers() {
        return CompletableFuture.supplyAsync(() ->
                teacherService.findAllTeachers().stream()
                        .map(Teacher::getVerificationKey)
                        .map(VerificationKey::getUser)
                        .map(scheduleService::recreateNextWeekEventsFromTemplatesForOwner)
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
    }
}
