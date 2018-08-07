package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class UserDTOToUser implements Converter<UserDTO, User> {
    @Override
    public User convert(final UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        return user;
    }
}
