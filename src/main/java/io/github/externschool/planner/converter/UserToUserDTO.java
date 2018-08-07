package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class UserToUserDTO implements Converter<User, UserDTO> {
    @Override
    public UserDTO convert(final User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO, "password");

        return userDTO;
    }
}
