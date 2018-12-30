package io.github.externschool.planner.emailservice;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;

public interface EmailService {
    void sendCancelEventMail(ScheduleEvent scheduleEvent);

    boolean emailIsValid(String email);
}
