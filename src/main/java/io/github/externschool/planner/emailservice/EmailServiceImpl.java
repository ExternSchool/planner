package io.github.externschool.planner.emailservice;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.Optional;

import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_PROPOSAL;
import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_SIGNATURE;
import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_SUBJECT;
import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_TEXT;
import static io.github.externschool.planner.util.Constants.FAKE_MAIL_DOMAIN;
import static io.github.externschool.planner.util.Constants.LOCALE;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendCancelEventMail(ScheduleEvent scheduleEvent){
        scheduleEvent.getParticipants().stream().filter(Objects::nonNull).forEach(participant -> {

            if (participant.getUser() == null
                    || participant.getUser().getEmail().split("[@._]")[1].equals(FAKE_MAIL_DOMAIN)) {
                System.out.println("\n\n\n Skipped to send to: "
                        + Optional.ofNullable(participant.getUser()).map(User::getEmail).orElse("No user")
                        + " \n\n\n");
                return;
            }
            User eventOwner = scheduleEvent.getOwner();
            String eventOwnersName = Optional.ofNullable(eventOwner.getVerificationKey())
                    .map(VerificationKey::getPerson)
                    .map(Person::getShortName)
                    .map(name -> " з " + name)
                    .orElse("");
            String eventDateTime =
                    scheduleEvent.getStartOfEvent()
                            .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(LOCALE))
                    + " о "
                    + scheduleEvent.getStartOfEvent()
                            .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(LOCALE));
            String textMessage = APPOINTMENT_CANCELLATION_TEXT
                    + eventDateTime
                    + eventOwnersName
                    + "\n"
                    + APPOINTMENT_CANCELLATION_PROPOSAL
                    + "\n\n"
                    + APPOINTMENT_CANCELLATION_SIGNATURE
                    +"\n"
                    + LocalDateTime.now()
                            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(LOCALE));
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(participant.getUser().getEmail());
            simpleMailMessage.setSubject(APPOINTMENT_CANCELLATION_SUBJECT + eventDateTime + eventOwnersName);
            simpleMailMessage.setFrom(eventOwner.getEmail());
            simpleMailMessage.setText(textMessage);

            mailSender.send(simpleMailMessage);
            System.out.println("\n\n\n Mail sent to: " + participant.getUser().getEmail() + " \n\n\n");
        });
    }
}
