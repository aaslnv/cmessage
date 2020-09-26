package kz.cmessage.core.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseDto<T> {

    private boolean success;
    private String message;
    private T data;
    @JsonIgnore
    private HttpStatus status;

    public ResponseDto(T data) {
        this.success = true;
        this.status = HttpStatus.OK;
        this.message = status.getReasonPhrase();
        this.data = data;
    }

    public ResponseDto(String message, T data, HttpStatus status) {
        this.success = true;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public ResponseDto(T data, HttpStatus status) {
        this.success = true;
        this.status = status;
        this.message = status.getReasonPhrase();
        this.data = data;
    }

    public ResponseDto(boolean success, String message, HttpStatus status) {
        this.success = success;
        this.message = message;
        this.status = status;
    }

    public ResponseDto(boolean success, HttpStatus status) {
        this.success = success;
        this.status = status;
        this.message = status.getReasonPhrase();
    }
}
