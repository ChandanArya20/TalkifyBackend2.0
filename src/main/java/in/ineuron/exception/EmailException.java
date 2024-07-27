package in.ineuron.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailException extends RuntimeException {
    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    public EmailException(int errorCode, String errorMessage, HttpStatus status) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.status = status;
    }
}
