package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class VerificationKeyFormatterTest {
    @Mock private VerificationKeyRepository repository;
    private VerificationKeyFormatter formatter;

    private VerificationKey key = new VerificationKey();

    @Before
    public void setup() {
        formatter = new VerificationKeyFormatter(repository);

        Mockito.when(repository.findByValue(key.getValue()))
                .thenReturn(key);
    }

    @Test
    public void shouldReturnSameKey_whenRunParsePrint() throws ParseException {
        Locale locale = new Locale("uk");

        VerificationKey actualKey = formatter.parse(formatter.print(key, locale), locale);

        assertThat(actualKey)
                .isNotNull()
                .isEqualTo(key)
                .isEqualToComparingFieldByField(key);
    }
}
