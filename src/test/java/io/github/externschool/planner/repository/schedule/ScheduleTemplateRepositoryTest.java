package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.entity.schedule.ScheduleTemplate;
import io.github.externschool.planner.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ScheduleTemplateRepositoryTest {
    @Autowired private ScheduleTemplateRepository templateRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ScheduleEventTypeRepository typeRepository;

    @Test
    public void shouldReturnList_whenFindByOwner() {
        User owner = new User("email@domain", "pass");
        ScheduleEventType type = new ScheduleEventType("Type", 1);
        ScheduleTemplate templateOne = new ScheduleTemplate(
                "First",
                "First Day Template",
                null,
                0,
                LocalTime.of(9, 0),
                LocalTime.of(9,45),
                LocalDateTime.now(),
                null,
                owner,
                type);
        ScheduleTemplate templateTwo = new ScheduleTemplate(
                "Second",
                "Second Day Template",
                null,
                1,
                LocalTime.of(9, 45),
                LocalTime.of(10,30),
                LocalDateTime.now(),
                null,
                owner,
                type);

        userRepository.save(owner);
        typeRepository.save(type);
        templateRepository.save(templateOne);
        templateRepository.save(templateTwo);

        List<ScheduleTemplate> actual = templateRepository.findAllByOwner(owner);

        assertThat(actual)
                .isNotEmpty()
                .containsExactlyInAnyOrder(templateOne, templateTwo);
    }
}
