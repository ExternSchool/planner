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
        expectedUser = new User(new Person(), email, "password");
    }

    @Test
    public void shouldReturnExpectedUser_WhenCreateUser() {
        userRepository.save(expectedUser);
        User actualUser = findWithEntityManager(expectedUser);

        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrProperty("person")
                .hasFieldOrProperty("email")
                .hasFieldOrProperty("roles")
                .hasFieldOrProperty("password")
                .hasNoNullFieldsOrProperties()
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
                .isNotNull()
                .hasNoNullFieldsOrProperties();
        assertThat(actualUser.getPassword()).isEqualTo(encryptedPassword);
    }

    @Test
    public void shouldReturnExpectedFields_WhenReadUserFields() {
        User actualUser = findWithEntityManager(expectedUser);

        assertThat(actualUser.getPerson())
                .isEqualTo(expectedUser.getPerson());
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
