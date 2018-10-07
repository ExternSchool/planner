package io.github.externschool.planner.emailservice;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    public JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCancelEventMail(ScheduleEvent scheduleEvent){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(scheduleEvent.getOwner().getEmail());
        simpleMailMessage.setSubject("Event cancellation");
        simpleMailMessage.setFrom(scheduleEvent.getOwner().getEmail());
        simpleMailMessage.setText("Sorry, may be next time)");
        mailSender.send(simpleMailMessage);
    }
}
