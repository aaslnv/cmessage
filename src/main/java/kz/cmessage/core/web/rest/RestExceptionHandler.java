package kz.cmessage.core.web.rest;

import kz.cmessage.core.common.dto.ResponseDto;
import kz.cmessage.core.exception.IllegalAccessException;
import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.exception.UnprocessableEntityException;
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
    @ExceptionHandler(UnprocessableEntityException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseDto<?> handleUnprocessableEntityException(UnprocessableEntityException ex) {
        return new ResponseDto<>(false, ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
