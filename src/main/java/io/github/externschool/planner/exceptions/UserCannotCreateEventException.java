package io.github.externschool.planner.exceptions;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class UserCannotCreateEventException extends RuntimeException {
    public UserCannotCreateEventException(String message) {
        super(message);
    }
}
