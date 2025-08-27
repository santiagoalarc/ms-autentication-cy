package co.com.crediya.exceptions;

import co.com.crediya.enums.UserErrorEnum;

public class UserException extends RuntimeException{

    private final UserErrorEnum error;

    public UserException(UserErrorEnum userErrorEnum) {
        super(userErrorEnum.name());
        this.error = userErrorEnum;
    }

    public UserErrorEnum getError() {
        return error;
    }

    public UserErrorEnum getErrorEnum() {
        return error;
    }
}
