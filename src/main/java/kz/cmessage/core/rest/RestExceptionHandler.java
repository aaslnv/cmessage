package kz.cmessage.core.rest;

import kz.cmessage.core.common.dto.ResponseDto;
import kz.cmessage.core.exception.BadRequestException;
import kz.cmessage.core.exception.IllegalAccessException;
import kz.cmessage.core.exception.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseDto<?> handleObjectNotFoundException(ObjectNotFoundException ex) {
        return new ResponseDto<>(false, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseDto<?> handleIllegalAccessException(IllegalAccessException ex) {
        return new ResponseDto<>(false, ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ResponseBody
    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<?> handleBadRequestException(BadRequestException ex) {
        return new ResponseDto<>(false, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
