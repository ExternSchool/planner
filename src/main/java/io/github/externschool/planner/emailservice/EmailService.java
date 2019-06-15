package io.github.externschool.planner.emailservice;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;

public interface EmailService {
    void sendCancelEventMail(ScheduleEvent scheduleEvent);

    void sendVerificationMail(User user);

    boolean emailIsValid(String email);
}
