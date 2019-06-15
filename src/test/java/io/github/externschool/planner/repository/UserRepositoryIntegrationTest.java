package io.github.externschool.planner.repository;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryIntegrationTest {
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private VerificationKeyRepository keyRepository;
    @Autowired private TestEntityManager entityManager;

    @Rule public PreparedDbRule db = EmbeddedPostgresRules
            .preparedDatabase(LiquibasePreparer.forClasspathLocation("liquibase/master-test.xml"));

    private final String email = "user@email.com";
    private User expectedUser;

    @Before
    public void setUp() {
        expectedUser = new User(email, "password");
    }

    @Test
    public void shouldReturnExpectedUser_WhenCreateUser() {
        userRepository.save(expectedUser);
        User actualUser = findWithEntityManager(expectedUser);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrProperty("email")
                .hasFieldOrProperty("roles")
                .hasFieldOrProperty("password")
                .isEqualTo(expectedUser);
    }

    @Test
    public void shouldReturnNull_WhenFindByEmailDeletedUser() {
        User actualUser = findWithEntityManager(expectedUser);
        userRepository.delete(actualUser);

        assertThat(userRepository.findByEmail(email)).isNull();

        userRepository.deleteAll();

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void shouldReturnExpectedUser_WhenSaveUser() {
        String encryptedPassword = "PASSWORD";
        expectedUser.setPassword(encryptedPassword);
        userRepository.save(expectedUser);
        User actualUser = findWithEntityManager(expectedUser);

        assertThat(actualUser)
                .isNotNull();
        assertThat(actualUser.getPassword()).isEqualTo(encryptedPassword);
    }

    @Test
    public void shouldReturnUserWithNewKey_WhenSaveUserTwice() {
        VerificationKey key1 = new VerificationKey();
        expectedUser.setVerificationKey(keyRepository.save(key1));
        userRepository.save(expectedUser);
        VerificationKey key2 = new VerificationKey();
        expectedUser.setVerificationKey(keyRepository.save(key2));
        userRepository.save(expectedUser);

        User actualUser = userRepository.findByEmail(expectedUser.getEmail());

        assertThat(actualUser.getVerificationKey())
                .isEqualTo(key2);
    }

    @Test
    public void shouldReturnExpectedFields_WhenReadUserFields() {
        User actualUser = findWithEntityManager(expectedUser);

        assertThat(actualUser.getEmail())
                .isEqualTo(email);
        assertThat(actualUser.getPassword())
                .isEqualTo(expectedUser.getPassword());
        assertThat(actualUser.getPassword())
                .isEqualTo(expectedUser.getPassword());
    }

    @Test
    public void shouldReturnExpectedRoles_WhenReadUserRoles() {
        Set<Role> expectedRoles = new HashSet<>(roleRepository.findAll());
        expectedRoles.forEach(expectedUser::addRole);
        User actualUser = findWithEntityManager(expectedUser);
        Set<Role> actualRoles = new HashSet<>(actualUser.getRoles());

        assertThat(actualRoles)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expectedRoles)
                .doesNotHaveDuplicates();
    }

    @Test
    public void shouldReturnExpectedRoles_WhenDeleteAddedUserRole() {
        Set<Role> expectedRoles = new HashSet<>(roleRepository.findAll());
        expectedRoles.forEach(expectedUser::addRole);
        Role removed = new Role("OFFICER");
        expectedUser.removeRole(removed);
        expectedRoles = new HashSet<>(expectedUser.getRoles());
        User actualUser = findWithEntityManager(expectedUser);
        Set<Role> actualRoles = new HashSet<>(actualUser.getRoles());

        assertThat(actualRoles)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expectedRoles)
                .doesNotContain(removed)
                .doesNotHaveDuplicates();
    }

    private User findWithEntityManager(User expectedUser) {
        entityManager.persist(expectedUser);
        Long actualId = expectedUser.getId();

        return entityManager.find(User.class, actualId);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }
}
