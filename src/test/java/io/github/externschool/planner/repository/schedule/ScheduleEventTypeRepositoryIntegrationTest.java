package io.github.externschool.planner.repository.schedule;

import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduleEventTypeRepositoryIntegrationTest {
    @Autowired private ScheduleEventTypeRepository repository;
    @Autowired private TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private ScheduleEventType oneType;

    @Before
    public void setUp() {
        oneType = new ScheduleEventType();
        oneType.setName("TestEventType");
        oneType.setAmountOfParticipants(1);
    }

    @Test
    public void shouldReturnListScheduleEventTypes() {
        int initialCount = (int)repository.count();
        entityManager.persist(oneType);
        List<ScheduleEventType> eventTypes = repository.findAll();

        assertThat(eventTypes)
                .isNotNull()
                .hasSize(initialCount + 1)
                .contains(oneType);
    }

    @Test
    public void shouldReturnEventTypeByName() {
        entityManager.persist(oneType);
        final ScheduleEventType eventType = repository.findByName(oneType.getName());

        assertThat(eventType)
                .isNotNull()
                .isEqualTo(oneType);
    }
}
