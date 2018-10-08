package io.github.externschool.planner.emailservice;

import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.util.Constants;
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
        simpleMailMessage.setSubject(Constants.APPOINTMENT_CANCELLATION_SUBJECT);
        simpleMailMessage.setFrom(scheduleEvent.getOwner().getEmail());
        simpleMailMessage.setText(Constants.APPOINTMENT_CANCELLATION_TEXT);
        mailSender.send(simpleMailMessage);
    }
}
