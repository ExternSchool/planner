package io.github.externschool.planner.converter;

import io.github.externschool.planner.PlannerApplication;
import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlannerApplication.class)
public class UserConverterTest {

     @Autowired
     private ConversionService conversionService;

     @Autowired
     private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

     private User expectedUser;
     private UserDTO userDTO;

     @Before
     public void setup(){
          final VerificationKey verificationKey = new VerificationKey();
          final String email = "u@u.com";
          final String password = passwordEncoder.encode("12345");

          expectedUser = new User();
          expectedUser.addVerificationKey(verificationKey);
          expectedUser.setEmail(email);
          expectedUser.setPassword(password);

          userDTO = new UserDTO();
          userDTO.setVerificationKey(verificationKey);
          userDTO.setEmail(email);
          userDTO.setPassword(password);
     }

     @Test
     public void shouldReturnUserDTO(){
          UserDTO actualDTO = conversionService.convert(expectedUser, UserDTO.class);

          assertThat(actualDTO.getEmail()).isEqualTo(expectedUser.getEmail());
          assertThat(actualDTO.getVerificationKey()).isEqualTo(expectedUser.getVerificationKey());
     }

     @Test
     public void shouldReturnExpectedUser(){
          User actualUser = conversionService.convert(userDTO, User.class);

          assertThat(actualUser.getEmail()).isEqualTo(userDTO.getEmail());
          assertThat(actualUser.getVerificationKey()).isEqualTo(userDTO.getVerificationKey());
          }
     }

