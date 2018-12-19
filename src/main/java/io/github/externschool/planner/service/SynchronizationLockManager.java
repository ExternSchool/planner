package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;

import java.util.concurrent.locks.Lock;

public interface SynchronizationLockManager {
    Lock getEventLock(ScheduleEvent e);
}
