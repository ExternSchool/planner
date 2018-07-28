package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserDTOToUser implements Converter<UserDTO, User> {

    @Autowired
    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public User convert(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        VerificationKey key = new VerificationKey();

        user.addVerificationKey(key);
        if(key != null && key.getPerson() != null){
            key.getPerson().addVerificationKey(key);
        }

        user.setEmail(userDTO.getEmail());
        user.setPassword(encoder.encode(userDTO.getPassword()));

        return user;
    }
}
