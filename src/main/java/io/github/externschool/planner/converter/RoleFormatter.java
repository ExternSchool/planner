package io.github.externschool.planner.converter;

import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_ROLE_NAMES;

@Service
public class RoleFormatter implements Formatter<Role> {
    private final RoleService roleService;
    private static final Map<String, String> ROLE_NAMES = UK_ROLE_NAMES.entrySet().stream()
       .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    @Autowired
    public RoleFormatter(final RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public Role parse(final String s, final Locale locale) {
        return roleService.getRoleByName(ROLE_NAMES.get(s));
    }

    @Override
    public String print(final Role role, final Locale locale) {
        return UK_ROLE_NAMES.get(role.getName());
    }
}
