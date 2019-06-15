package io.github.externschool.planner.factories;

import io.github.externschool.planner.entity.Role;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class RolesFactory {
    private RolesFactory() {}

    public static final String ROLE_ALLOWED_CREATE_EVENT = "AllowedCreateEvent";
    public static final String ROLE_NOT_ALLOWED_CREATE_EVENT = "NotAllowedCreateEvent";

    public static Role createRoleEntity(final String name) {
        return new Role(name);
    }
}
