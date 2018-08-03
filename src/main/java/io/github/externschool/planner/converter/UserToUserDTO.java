package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class UserToUserDTO implements Converter<User, UserDTO> {
    @Autowired VerificationKeyService keyService;

    @Override
    public UserDTO convert(final User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setVerificationKeyValue(user.getVerificationKey() != null ? user.getVerificationKey().getValue() : "");
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());

        return userDTO;
    }
}
