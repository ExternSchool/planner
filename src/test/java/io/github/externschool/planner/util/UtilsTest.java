package io.github.externschool.planner.util;

import io.github.externschool.planner.dto.PersonDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsTest {

    @Test
    public void shouldReturnEmptyList_whenFilterDoesNotMatch() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setLastName("test");
        String request = "DontMatch";
        List<? extends PersonDTO> list = Utils.searchRequestFilter(Collections.singletonList(personDTO), request);

        assertThat(list)
                .isEmpty();
    }

    @Test
    public void shouldReturnList_whenFilterMatches() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setLastName("test");
        String request = "test";
        List<? extends PersonDTO> list = Utils.searchRequestFilter(Collections.singletonList(personDTO), request);

        assertThat(list)
                .isNotEmpty();
        assertThat(list.get(0))
                .isInstanceOf(PersonDTO.class)
                .isEqualTo(personDTO);
    }
}
