package cz.upol.logicgo.exceptions.database;

import cz.upol.logicgo.exceptions.LogicGoException;

import java.io.Serial;

public class UserCreateException extends LogicGoException {

    @Serial
    private static final long serialVersionUID = 872684765439219059L;

    public UserCreateException() {
    }

    public UserCreateException(String message) {
        super(message);
    }

    public UserCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
