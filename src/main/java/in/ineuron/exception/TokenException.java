package in.ineuron.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenException extends RuntimeException {
    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    public TokenException(int errorCode, String errorMessage, HttpStatus status) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.status = status;
    }
}
