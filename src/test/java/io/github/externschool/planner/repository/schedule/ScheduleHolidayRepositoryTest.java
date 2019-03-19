package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.schedule.ScheduleHoliday;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduleHolidayRepositoryTest {
    @Autowired ScheduleHolidayRepository repository;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    @Test
    public void shouldReturnLocalDate_whenSave() {
        ScheduleHoliday holiday = new ScheduleHoliday(LocalDate.now(), LocalDate.now().plusDays(2L));

        ScheduleHoliday saved = repository.save(holiday);

        assertThat(saved)
                .isEqualToComparingFieldByField(holiday);
    }

    @Test
    public void shouldReturnList_whenGetAllHolidaysBetweenDates() {
        ScheduleHoliday first = new ScheduleHoliday(LocalDate.now(),LocalDate.now().plusDays(2L));
        ScheduleHoliday second = new ScheduleHoliday(LocalDate.now().plusDays(1L),LocalDate.now().plusDays(2L));
        List<ScheduleHoliday> holidays = Arrays.asList(first, second);

        repository.saveAll(holidays);

        List<ScheduleHoliday> firstOnly = repository.findAllByHolidayDateBetween(
                LocalDate.now(),
                LocalDate.now());
        List<ScheduleHoliday> bothDays = repository.findAllByHolidayDateBetween(
                LocalDate.now(),
                LocalDate.now().plusDays(1L));
        List<ScheduleHoliday> secondOnly = repository.findAllByHolidayDateBetween(
                LocalDate.now().plusDays(1L),
                LocalDate.now().plusDays(1L));
        List<ScheduleHoliday> noDays = repository.findAllByHolidayDateBetween(
                LocalDate.now().plusDays(2L),
                LocalDate.now().plusDays(3L));

        assertThat(firstOnly)
                .containsOnly(first);
        assertThat(bothDays)
                .containsExactly(first, second);
        assertThat(secondOnly)
                .containsOnly(second);
        assertThat(noDays)
                .isEmpty();
    }

    @Test
    public void shouldReturnList_whenGetAllSubstitutionsBetweenDates() {
        ScheduleHoliday first = new ScheduleHoliday(LocalDate.now().minusDays(1L),LocalDate.now());
        ScheduleHoliday second = new ScheduleHoliday(LocalDate.now().minusDays(2L),LocalDate.now().plusDays(1L));
        List<ScheduleHoliday> holidays = Arrays.asList(first, second);

        repository.saveAll(holidays);

        List<ScheduleHoliday> firstOnly = repository.findAllBySubstitutionDateBetween(
                LocalDate.now(),
                LocalDate.now());
        List<ScheduleHoliday> bothDays = repository.findAllBySubstitutionDateBetween(
                LocalDate.now(),
                LocalDate.now().plusDays(1L));
        List<ScheduleHoliday> secondOnly = repository.findAllBySubstitutionDateBetween(
                LocalDate.now().plusDays(1L),
                LocalDate.now().plusDays(1L));
        List<ScheduleHoliday> noDays = repository.findAllBySubstitutionDateBetween(
                LocalDate.now().plusDays(2L),
                LocalDate.now().plusDays(3L));

        assertThat(firstOnly)
                .containsOnly(first);
        assertThat(bothDays)
                .containsExactly(first, second);
        assertThat(secondOnly)
                .containsOnly(second);
        assertThat(noDays)
                .isEmpty();
    }
}
