package kz.cmessage.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class IllegalAccessException extends RuntimeException {

    public IllegalAccessException(String message) {
        super(message);
    }
}
