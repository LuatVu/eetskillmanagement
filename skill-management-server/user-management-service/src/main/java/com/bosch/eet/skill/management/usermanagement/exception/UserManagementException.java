package com.bosch.eet.skill.management.usermanagement.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserManagementException extends Exception{

    private static final long serialVersionUID = 1L;

    private String code;
    private String message;
    private Throwable cause;

    public UserManagementException() {
    }

    public UserManagementException(final String message) {
        this.message = message;
    }

    public UserManagementException(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public UserManagementException(final String code, final String message, Throwable cause) {
        this.code = code;
        this.message = message;
        this.cause = cause;
    }

    @Override
    public String toString() {
        return String.format("UserServiceException[code=%s, message='%s', cause='%s']", code, message, cause);
    }
}
