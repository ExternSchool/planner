package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.entity.schedule.ScheduleTemplate;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduleTemplateRepositoryTest {
    @Autowired private ScheduleTemplateRepository repository;
    @Autowired private TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    @Test
    public void shouldReturnList_whenFindByOwner() {
        User owner = new User("email@domain", "pass");
        ScheduleEventType type = new ScheduleEventType("Type", 1);
        ScheduleTemplate templateOne = new ScheduleTemplate(
                "First",
                "Monday Template",
                null,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(9,45),
                LocalDateTime.now(),
                null,
                owner,
                type);
        ScheduleTemplate templateTwo = new ScheduleTemplate(
                "Second",
                "Tuesday Template",
                null,
                DayOfWeek.TUESDAY,
                LocalTime.of(9, 45),
                LocalTime.of(10,30),
                LocalDateTime.now(),
                null,
                owner,
                type);
        entityManager.persist(owner);
        entityManager.persist(type);
        repository.save(templateOne);
        repository.save(templateTwo);

        List<ScheduleTemplate> actual = repository.findAllByOwner(owner);

        assertThat(actual)
                .isNotEmpty()
                .containsExactlyInAnyOrder(templateOne, templateTwo);
    }
}
