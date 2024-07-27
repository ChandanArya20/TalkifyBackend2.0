package in.ineuron.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class InvalidRequestDataException extends RuntimeException {
    private int errorCode;
    private String errorMessage;
    private HttpStatus status;

    public InvalidRequestDataException(int errorCode, String errorMessage, HttpStatus status) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.status = status;
    }
}
