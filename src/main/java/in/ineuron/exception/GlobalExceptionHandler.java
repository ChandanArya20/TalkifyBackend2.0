package in.ineuron.exception;

import in.ineuron.constant.ErrorConstant;
import in.ineuron.dto.ErrorDetails;
import in.ineuron.utils.TalkifyUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private TalkifyUtils talkifyUtils;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> requestDataFieldErrorHandler(MethodArgumentNotValidException exception ){

        List<FieldError> fieldErrors = exception.getFieldErrors();

        Map<String, String> errorsMap = new HashMap<>();
        fieldErrors.forEach(fieldError -> {
                    errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                });

        ErrorDetails errorDetails = new ErrorDetails(
                ErrorConstant.INVALID_REQUEST_FIELD_ERROR.getErrorCode(),
                talkifyUtils.formatFieldError(errorsMap));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
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
