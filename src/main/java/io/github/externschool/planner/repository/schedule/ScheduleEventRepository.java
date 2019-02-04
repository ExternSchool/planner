package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.schedule.ScheduleEvent;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleEventRepository extends JpaRepository<ScheduleEvent, Long> {
    List<ScheduleEvent> findAllByOwnerAndStartOfEventBetweenOrderByStartOfEvent(User owner,
                                                                                LocalDateTime startOfEventBeginning,
                                                                                LocalDateTime startOfEventEnding);

    List<ScheduleEvent> findAllByOwner(User owner);

    List<ScheduleEvent> findAllByType(ScheduleEventType type);
}
