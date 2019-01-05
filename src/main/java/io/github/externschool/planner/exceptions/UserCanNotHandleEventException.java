package io.github.externschool.planner.exceptions;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class UserCanNotHandleEventException extends RuntimeException {
    public UserCanNotHandleEventException(String message) {
        super(message);
    }
}
