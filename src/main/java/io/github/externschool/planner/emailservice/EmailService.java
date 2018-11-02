package io.github.externschool.planner.emailservice;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_SUBJECT;
import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_TEXT;

@Component
public class EmailService {

    private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCancelEventMail(ScheduleEvent scheduleEvent){
        scheduleEvent.getParticipants().stream().filter(Objects::nonNull).forEach(participant -> {
            User sender = scheduleEvent.getOwner();
            String textMessage = APPOINTMENT_CANCELLATION_TEXT
                    + Optional.ofNullable(sender.getVerificationKey().getPerson()).map(Person::getShortName);
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(participant.getEmail());
            simpleMailMessage.setSubject(APPOINTMENT_CANCELLATION_SUBJECT);
            simpleMailMessage.setFrom(sender.getEmail());
            simpleMailMessage.setText(textMessage);

            mailSender.send(simpleMailMessage);
        });
    }
}
