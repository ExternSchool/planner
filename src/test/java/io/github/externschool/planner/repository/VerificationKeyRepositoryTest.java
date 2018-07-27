package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.VerificationKey;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class VerificationKeyRepositoryTest {

    @Autowired
    private VerificationKeyRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private VerificationKey key1;
    private VerificationKey key2;
    private VerificationKey key3;

    @Before
    public void setUp() {
        key1 = new VerificationKey();
        key2 = new VerificationKey();
        key3 = new VerificationKey();

        entityManager.persist(key1);
        entityManager.persist(key2);
        entityManager.persist(key3);
    }

    @Test
    public void shouldReturnListOfKey_WhenFindAll() {
        List<VerificationKey> keyList = this.repository.findAll();

        assertThat(keyList)
                .isNotNull()
                .hasSize(3)
                .contains(key1, key2, key3);
    }

    @Test
    public void shouldReturnKey_WhenGetById() {
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
    public void getNull_whenFindDeletedTeacher() {
        repository.delete(key1);
        VerificationKey actualKey = repository.findById(key1.getId()).orElse(null);

        assertThat(actualKey)
                .isNull();
    }
}
