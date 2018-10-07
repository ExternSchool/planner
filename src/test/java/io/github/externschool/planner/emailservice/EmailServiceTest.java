package io.github.externschool.planner.emailservice;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    private ScheduleEvent scheduleEvent;
    private User user;

    @Before
    public void setup(){
        user = new User();
        user.setEmail("extern.school@gmail.com");

        scheduleEvent = new ScheduleEvent();
        scheduleEvent.setOwner(user);
        scheduleEvent.setCancelled(true);
    }

    @Test
    public void shouldSendCancellationMail(){

        emailService.sendCancelEventMail(scheduleEvent);
    }
}
