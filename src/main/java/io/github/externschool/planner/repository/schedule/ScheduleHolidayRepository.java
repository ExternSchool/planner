package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.schedule.ScheduleHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleHolidayRepository extends JpaRepository<ScheduleHoliday, Long> {
    List<ScheduleHoliday> findAllByHolidayDateBetween(LocalDate start, LocalDate end);

    List<ScheduleHoliday> findAllBySubstitutionDateBetween(LocalDate start, LocalDate end);
}
