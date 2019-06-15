package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.VerificationKey;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VerificationKeyRepositoryTest {
    @Autowired private VerificationKeyRepository repository;
    @Autowired TestEntityManager entityManager;

    @Rule public ExpectedException thrown = ExpectedException.none();
    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private List<VerificationKey> keys;

    @Before
    public void setUp() {
        keys = Arrays.asList(new VerificationKey(), new VerificationKey(), new VerificationKey());
    }

    @Test
    public void shouldReturnListOfKey_WhenFindAll() {
        int initialCount = (int)repository.count();
        repository.saveAll(keys);
        List<VerificationKey> keyList = this.repository.findAll();

        assertThat(keyList)
                .isNotNull()
                .hasSize(initialCount + keys.size())
                .containsAll(keys);
    }

    @Test
    public void shouldReturnKey_WhenGetById() {
        VerificationKey key1 = keys.get(0);
        repository.saveAll(keys);
        VerificationKey actualKey = repository.findById(key1.getId()).orElse(null);

        assertThat(actualKey)
                .isNotNull()
                .isEqualTo(key1)
                .isEqualToComparingFieldByField(key1);
    }

    @Test
    public void shouldReturnKey_WhenSaveOrUpdateKey() {
        VerificationKey expectedKey = new VerificationKey();
        repository.save(expectedKey);
        VerificationKey actualKey = repository.findById(expectedKey.getId()).orElse(null);

        assertThat(actualKey)
                .isNotNull()
                .isEqualTo(expectedKey)
                .isEqualToComparingFieldByField(expectedKey);
    }

    @Test
    public void getNull_whenFindDeletedKey() {
        repository.saveAll(keys);
        VerificationKey key1 = keys.get(0);

        repository.delete(key1);
        VerificationKey actualKey = repository.findById(key1.getId()).orElse(null);

        assertThat(actualKey)
                .isNull();
    }
}
