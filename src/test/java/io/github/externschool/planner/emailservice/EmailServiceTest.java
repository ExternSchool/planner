package io.github.externschool.planner.emailservice;

import io.github.externschool.planner.entity.Participant;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.factories.RolesFactory;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_PROPOSAL;
import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_SIGNATURE;
import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_SUBJECT;
import static io.github.externschool.planner.util.Constants.APPOINTMENT_CANCELLATION_TEXT;
import static io.github.externschool.planner.util.Constants.LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EmailServiceTest {
    @Mock private JavaMailSender mailSender;
    private EmailService emailService;

    private ScheduleEvent scheduleEvent;
    private User user;
    private List<String> arguments;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        emailService = new EmailServiceImpl(mailSender);

        arguments = new ArrayList<>();
        doAnswer((Answer<Void>) invocation -> {
            Object[] args = invocation.getArguments();
            arguments.add(Arrays.toString(args));
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        user = new User();
        user.setEmail("extern.school@gmail.com");
        VerificationKey key = new VerificationKey();
        user.addVerificationKey(key);
        Person person = new Person();
        person.addVerificationKey(key);
        person.setFirstName("FirstName");
        person.setPatronymicName("PatronymicName");
        person.setLastName("LastName");

        scheduleEvent = new ScheduleEvent();
        scheduleEvent.setOwner(user);
        scheduleEvent.setCancelled(true);
    }

    @Test
    public void shouldReturnOnlyOneGivenMessage_whenSendCancellationMail(){
        long id = 1L;
        Role userRole = RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT);
        ScheduleEvent event = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        event.setOwner(user);
        event.getType().setAmountOfParticipants(2);
        event.setId(id);
        event.setOpen(true);
        event.getType().addParticipant(userRole);
        User user1 = new User("participant@email.com", "pass");
        user1.setId(++id);
        User user2 = new User("emailFake@x", "pass");
        user2.setId(++id);
        Arrays.asList(user1, user2).forEach(u -> {
            u.addRole(userRole);
            Participant participant = new Participant(u, event);
            participant.setId(u.getId() + 2L);
        });
        User eventOwner = event.getOwner();
        String eventOwnersName = " з " + eventOwner.getVerificationKey().getPerson().getShortName();
        String eventDateTime =
                event.getStartOfEvent()
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(LOCALE))
                        + " о "
                        + event.getStartOfEvent()
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
        String subject = APPOINTMENT_CANCELLATION_SUBJECT + eventDateTime + eventOwnersName;

        assertThat(event.getParticipants())
                .hasSize(2);

        emailService.sendCancelEventMail(event);

        assertThat(arguments)
                .hasSize(1);
        assertThat(arguments.get(0))
                .isNotBlank();
        assertThat(arguments.get(0))
                .containsSubsequence(
                        "from=" + eventOwner.getEmail(),
                        "to=" + user1.getEmail(),
                        "subject=" + subject,
                        "text=" + textMessage);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void shouldReturnTwoMessages_whenSendCancellationMail(){
        long id = 1L;
        Role userRole = RolesFactory.createRoleEntity(RolesFactory.ROLE_ALLOWED_CREATE_EVENT);
        ScheduleEvent event = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
        event.setOwner(user);
        event.getType().setAmountOfParticipants(2);
        event.setId(id);
        event.setOpen(true);
        event.getType().addParticipant(userRole);
        User user1 = new User("participant@email.com", "pass");
        user1.setId(++id);
        User user2 = new User("emailOk@anothermail", "pass");
        user2.setId(++id);
        Arrays.asList(user1, user2).forEach(u -> {
            u.addRole(userRole);
            Participant participant = new Participant(u, event);
            participant.setId(u.getId() + 2L);
        });
        User eventOwner = event.getOwner();
        String eventOwnersName = " з " + eventOwner.getVerificationKey().getPerson().getShortName();
        String eventDateTime =
                event.getStartOfEvent()
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(LOCALE))
                        + " о "
                        + event.getStartOfEvent()
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
        String subject = APPOINTMENT_CANCELLATION_SUBJECT + eventDateTime + eventOwnersName;

        assertThat(event.getParticipants())
                .hasSize(2);

        emailService.sendCancelEventMail(event);

        assertThat(arguments)
                .hasSize(2);
        assertThat(arguments.get(0))
                .isNotBlank();
        assertThat(arguments.get(0))
                .containsSubsequence(
                        "from=" + eventOwner.getEmail(),
                        "to=" + user1.getEmail(),
                        "subject=" + subject,
                        "text=" + textMessage);
        assertThat(arguments.get(1))
                .isNotBlank();
        assertThat(arguments.get(1))
                .containsSubsequence(
                        "from=" + eventOwner.getEmail(),
                        "to=" + user2.getEmail(),
                        "subject=" + subject,
                        "text=" + textMessage);

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void shouldReturnTrue_ifEmailIsValid() {
        assertThat(emailService.emailIsValid("some@email"))
                .isEqualTo(true);
    }

    @Test
    public void shouldReturnFalse_ifEmailIsFake() {
        assertThat(emailService.emailIsValid("ab2fc006-7a24-4cb9-98b7-296369a23a42@x"))
                .isEqualTo(false);
    }

    @Test
    public void shouldReturnFalse_ifEmailIsNull() {
        assertThat(emailService.emailIsValid(null))
                .isEqualTo(false);
    }

    @Test
    public void shouldReturnFalse_ifEmailIsEmpty() {
        assertThat(emailService.emailIsValid(""))
                .isEqualTo(false);
    }

    @Test
    public void shouldReturnFalse_ifEmailIsInvalid() {
        assertThat(emailService.emailIsValid("fjhbaflsjfbhsal"))
                .isEqualTo(false);
    }
}
