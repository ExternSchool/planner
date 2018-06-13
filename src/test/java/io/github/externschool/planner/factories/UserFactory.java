package io.github.externschool.planner.factories;

import io.github.externschool.planner.entity.User;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public final class UserFactory {
    private UserFactory() {}

    public static final Long USER_ID = 1L;
    public static final String USER_EMAIL = "user@email.com";
    public static final String USER_PASSWORD = "TestPassword";

    public static User createUser() {
        return new User(
                USER_EMAIL,
                USER_PASSWORD
        );

    }

}
