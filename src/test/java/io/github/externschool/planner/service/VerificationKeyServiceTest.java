package io.github.externschool.planner.service;

import io.github.externschool.planner.TestPlannerApplication;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestPlannerApplication.class)
public class VerificationKeyServiceTest {

    @MockBean
    private VerificationKeyRepository repository;

    @Autowired
    private VerificationKeyService service;

    private VerificationKey expectedKey;

    @Before
    public void setUp() {
        expectedKey = new VerificationKey();
        expectedKey.setId(1L);
    }

    @Test
    public void shouldReturnKey_whenFindKeyById() {
        Mockito.when(repository.findById(expectedKey.getId()))
                .thenReturn(Optional.of(expectedKey));

        VerificationKey actualKey = service.findKeyById(expectedKey.getId());

        assertThat(actualKey)
                .isNotNull()
                .isEqualTo(expectedKey)
                .isEqualToComparingFieldByField(expectedKey);
    }

    @Test
    public void shouldReturnList_whenFindAll() {
        List<VerificationKey> expectedList = new ArrayList<>();
        expectedList.add(expectedKey);

        Mockito.when(repository.findAll())
                .thenReturn(expectedList);

        List<VerificationKey> actualList = service.findAll();

        assertThat(actualList)
                .isNotNull()
                .isInstanceOf(ArrayList.class);
        assertThat(actualList.get(0))
                .isNotNull()
                .isEqualTo(expectedList.get(0))
                .isEqualToComparingFieldByField(expectedList.get(0));
    }

    @Test
    public void shouldReturnKey_whenSaveOrUpdateKey() {
        Mockito.when(repository.save(expectedKey))
                .thenReturn(expectedKey);

        VerificationKey actualKey = service.saveOrUpdateKey(expectedKey);

        assertThat(actualKey)
                .isNotNull()
                .isEqualTo(expectedKey)
                .isEqualToComparingFieldByField(expectedKey);
    }

    @Test
    public void shouldInvokeOnce_whenDeleteKey() {
        Optional<VerificationKey> optional = Optional.of(expectedKey);
        Mockito.when(repository.findById(expectedKey.getId()))
                .thenReturn(optional)
                .thenReturn(null);

        service.deleteById(expectedKey.getId());

        verify(repository, times(1)).delete(expectedKey);
    }
}
