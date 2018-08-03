package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.exceptions.KeyNotValidException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class UserDTOToUser implements Converter<UserDTO, User> {
    @Override
    public User convert(final UserDTO userDTO) throws KeyNotValidException {
        User user = new User();
        user.setId(userDTO.getId());
        user.addVerificationKey(userDTO.getVerificationKey());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        return user;
    }
}
