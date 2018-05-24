package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    public void createNewUserTest(){

        User dmytro = new User();

        dmytro.setEmail("dmytro@gmail.com");

        userService.createNewUser(dmytro);

        User retrieveDmytro = userRepository.findByEmail("dmytro@gmail.com");

        assertThat(retrieveDmytro.getEmail()).isEqualTo(dmytro.getEmail());
    }
}
