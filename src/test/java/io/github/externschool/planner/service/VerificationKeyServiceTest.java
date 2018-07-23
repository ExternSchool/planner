package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VerificationKeyServiceTest {

    @Mock
    private VerificationKeyRepository repository;

    @InjectMocks
    private VerificationKeyServiceImpl service;

    private VerificationKey expectedKey;

    @Before
    public void setUp() {
        expectedKey = new VerificationKey();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnKey_whenFindKeyById() {
        Mockito.when(repository.getById(expectedKey.getId()))
                .thenReturn(expectedKey);

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
    public void getNull_whenDeleteKey() {
        repository.save(expectedKey);
        repository.delete(expectedKey);
        VerificationKey actualKey = repository.getById(expectedKey.getId());

        assertThat(actualKey)
                .isNull();
    }
}
