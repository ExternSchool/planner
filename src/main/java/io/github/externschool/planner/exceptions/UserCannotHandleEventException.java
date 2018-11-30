package io.github.externschool.planner.exceptions;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class UserCannotHandleEventException extends RuntimeException {
    public UserCannotHandleEventException(String message) {
        super(message);
    }
}
