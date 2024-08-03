package in.ineuron.exception;

import in.ineuron.constant.ErrorConstant;
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

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetails> userExceptionHandler(UserException exception){

        ErrorDetails errorDetails = new ErrorDetails(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getStatus()).body(errorDetails);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorDetails> tokenExceptionHandler(TokenException exception){

        ErrorDetails errorDetails = new ErrorDetails(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getStatus()).body(errorDetails);
    }

    @ExceptionHandler(InvalidRequestDataException.class)
    public ResponseEntity<ErrorDetails> InvalidRequestHandler(InvalidRequestDataException exception){

        ErrorDetails errorDetails = new ErrorDetails(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getStatus()).body(errorDetails);
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorDetails> messageExceptionHandler(MessageException exception ){

        ErrorDetails errorDetails = new ErrorDetails(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getStatus()).body(errorDetails);
    }

    @ExceptionHandler(OTPException.class)
    public ResponseEntity<ErrorDetails> OTPExceptionHandler(OTPException exception ){

        ErrorDetails errorDetails = new ErrorDetails(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getStatus()).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> globalExceptionHandler(Exception exception){
        exception.printStackTrace();
        ErrorDetails errorDetails = new ErrorDetails(
                ErrorConstant.GENERIC_ERROR.getErrorCode(),
                ErrorConstant.GENERIC_ERROR.getErrorMessage()+" "+exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }



}
