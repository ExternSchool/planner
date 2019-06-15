package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Repository
public interface ScheduleEventTypeRepository extends JpaRepository<ScheduleEventType, Long> {
    ScheduleEventType findByName(String name);
}
