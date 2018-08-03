package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.exceptions.KeyNotValidException;
import io.github.externschool.planner.repository.VerificationKeyRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserConvertersTest {
    @Autowired private ConversionService conversionService;
    @Autowired private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private User expectedUser;
    private UserDTO expectedDTO;
    private VerificationKey key;

    @Before
    public void setup() {
        key = new VerificationKey();

        expectedUser = new User("user@email.com", "pass");
        expectedUser.setId(1L);
        expectedUser.addVerificationKey(key);

        expectedDTO = new UserDTO();
        expectedDTO.setId(1L);
        expectedDTO.setEmail(expectedUser.getEmail());
        expectedDTO.setPassword(expectedUser.getPassword());
        expectedDTO.setVerificationKey(key);
    }

    @Test
    public void shouldReturnUserDTO_whenConvertUser() {
        UserDTO actualDTO = conversionService.convert(expectedUser, UserDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedDTO);
    }

    @Test
    public void shouldReturnExpectedUser_whenConvertUserDTO() throws KeyNotValidException {
        User actualUser = conversionService.convert(expectedDTO, User.class);

        assertThat(actualUser)
                .isNotNull()
                .isEqualToComparingFieldByField(expectedUser);
    }
}