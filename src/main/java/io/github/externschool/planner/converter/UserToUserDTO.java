package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserToUserDTO implements Converter<User, UserDTO> {

    @Autowired
    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDTO convert(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setVerificationKey(user.getVerificationKey());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(encoder.encode(user.getPassword()));

        return userDTO;
    }
}
