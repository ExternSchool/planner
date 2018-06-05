package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.profile.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final String email = "user@email.com";
    private User expectedUser;

    @Before
    public void setUp(){
        expectedUser = new User(new Person(), email, "password", "encrypted");
    }

    @Test
    public void testCreateReadDeleteUser() {
        User actualUser = getUser(expectedUser);

        assertThat(actualUser).isNotNull();
        assertThat(actualUser).hasFieldOrProperty("person");
        assertThat(actualUser).hasFieldOrProperty("email");
        assertThat(actualUser).hasFieldOrProperty("roles");
        assertThat(actualUser).hasFieldOrProperty("encryptedPassword");
        assertThat(actualUser).hasNoNullFieldsOrProperties();
        assertThat(actualUser).isEqualTo(expectedUser);
        assertThat(actualUser.getPerson()).isEqualTo(expectedUser.getPerson());
        assertThat(actualUser.getEmail()).isEqualTo(email);
        assertThat(actualUser.getPassword()).isEqualTo(expectedUser.getPassword());
        assertThat(actualUser.getEncryptedPassword()).isEqualTo(expectedUser.getEncryptedPassword());

        userRepository.delete(actualUser);

        assertThat(userRepository.findByEmail(email)).isNull();

        userRepository.deleteAll();

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void testUpdateUser() {
        String encryptedPassword = "PASSWORD";
        expectedUser.setEncryptedPassword(encryptedPassword);

        User actualUser = getUser(expectedUser);

        assertThat(actualUser).isNotNull();
        assertThat(actualUser).hasNoNullFieldsOrProperties();
        assertThat(actualUser.getEncryptedPassword()).isEqualTo(encryptedPassword);
    }

    @Test
    public void testUserRoles() {
        final Set<Role> expectedRoles = new HashSet<>();
        roleRepository.findAll().forEach(expectedRoles::add);
        expectedRoles.forEach(expectedUser::addRole);

        User actualUser = getUser(expectedUser);
        Set<Role> actualRoles = new HashSet<>(actualUser.getRoles());

        assertThat(actualRoles).isNotEmpty();
        assertThat(actualRoles).containsAll(expectedRoles);

        Role removed = new Role("officer");
        expectedUser.removeRole(removed);

        actualUser = getUser(expectedUser);
        actualRoles = new HashSet<>(actualUser.getRoles());

        assertThat(actualRoles).isNotEmpty();
        assertThat(actualRoles).containsAll(expectedUser.getRoles());
        assertThat(actualRoles).doesNotContain(removed);
        assertThat(actualRoles).doesNotHaveDuplicates();
    }

    private User getUser(User expectedUser) {
        userRepository.save(expectedUser);
        Long actualId = expectedUser.getId();

        return entityManager.find(User.class, actualId);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }
}
