package io.github.externschool.planner.util;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.factories.schedule.ScheduleEventFactory;
import io.github.externschool.planner.service.ScheduleService;
import io.github.externschool.planner.service.TeacherService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.timing.Pause.pause;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ScheduledTasksTest {
    @Mock private ScheduleService scheduleService;
    @Mock private TeacherService teacherService;
    private ScheduledTasks scheduledTasks;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        scheduledTasks = new ScheduledTasks(scheduleService, teacherService);
    }

    @Test
    public void shouldCreateEvents_whenDoScheduledWork() {
        List<Teacher> teachers = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<ScheduleEvent> expectedEvents = new ArrayList<>();
        Map<User, ScheduleEvent> events = new HashMap<>();

        for(String name : Arrays.asList("A", "B", "C")) {
            Teacher teacher = new Teacher();
            teacher.setLastName(name);
            VerificationKey key = new VerificationKey();
            User user = new User();
            Role role = new Role("ROLE_TEACHER");
            user.addRole(role);
            user.setEmail(name + "@x");
            teacher.addVerificationKey(key);
            user.addVerificationKey(key);
            ScheduleEvent event = ScheduleEventFactory.createNewScheduleEventWithoutParticipants();
            event.setOwner(user);
            user.addOwnEvent(event);
            long id = Math.abs(key.hashCode());
            teacher.setId(++id);
            user.setId(++id);
            event.setId(++id);

            teachers.add(teacher);
            users.add(user);
            events.put(user, event);
            expectedEvents.add(event);
        }

        Mockito.when(teacherService.findAllTeachers()).thenReturn(teachers);
        Mockito.when(scheduleService.recreateNextWeekEventsFromTemplatesForOwner(any(User.class)))
                .thenAnswer(i -> Collections.singletonList(events.get(i.getArguments()[0])));

        assertThat(teacherService.findAllTeachers())
                .containsExactlyElementsOf(teachers);
        users.forEach(user -> {
            assertThat(scheduleService.recreateNextWeekEventsFromTemplatesForOwner(user))
                    .isEqualTo(Collections.singletonList(user.getOwnEvents().stream().findFirst().get()));
        });

        CompletableFuture<List<ScheduleEvent>> future = scheduledTasks.recreateEventsForAllTeachers();
        pause(100);

        try {
            assertThat(future.get())
                    .containsExactlyElementsOf(expectedEvents);

            assertThat(future)
                    .isCompleted()
                    .isCompletedWithValueMatching(result -> result.containsAll(expectedEvents))
                    .isDone();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
