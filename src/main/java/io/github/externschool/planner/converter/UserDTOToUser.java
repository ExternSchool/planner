package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class UserDTOToUser implements Converter<UserDTO, User> {
    @Autowired VerificationKeyService keyService;

    @Override
    public User convert(final UserDTO userDTO) {
        User user = new User();

        return user;
    }
}
