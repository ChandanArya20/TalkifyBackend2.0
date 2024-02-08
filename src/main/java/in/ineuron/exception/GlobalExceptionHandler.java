package in.ineuron.exception;

import in.ineuron.dto.ErrorDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> userExceptionHandler(UserNotFoundException exception){

        ErrorDetails errorDetails = new ErrorDetails(exception.toString(), exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<ErrorDetails> unauthorizedUserExceptionHandler(UserNotAuthorizedException exception){

        ErrorDetails errorDetails = new ErrorDetails(exception.toString(), exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorDetails> tokenNotFoundExceptionHandler(TokenException exception){

        ErrorDetails errorDetails = new ErrorDetails(exception.toString(), exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(InvalidRequestDataException.class)
    public ResponseEntity<ErrorDetails> InvalidRequestDataExceptionHandler(InvalidRequestDataException exception){

        ErrorDetails errorDetails = new ErrorDetails(exception.toString(), exception.getErrorResults().toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> userExceptionHandler(BadCredentialsException exception){

        ErrorDetails errorDetails = new ErrorDetails(exception.toString(), exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorDetails> userExceptionHandler(MessageNotFoundException exception ){

        ErrorDetails errorDetails = new ErrorDetails(exception.toString(), exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> globalExceptionHandler(Exception exception){
        exception.printStackTrace();
        ErrorDetails errorDetails = new ErrorDetails(exception.toString(), exception.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus. INTERNAL_SERVER_ERROR).body(errorDetails);
    }



}
