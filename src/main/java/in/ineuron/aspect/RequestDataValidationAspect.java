package in.ineuron.aspect;

import in.ineuron.constant.ErrorConstant;
import in.ineuron.exception.InvalidRequestDataException;
import in.ineuron.utils.TalkifyUtils;
import in.ineuron.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Aspect
@Component
@AllArgsConstructor
public class RequestDataValidationAspect {

    private final UserUtils userUtils;
    private final TalkifyUtils talkifyUtils;

    @Before("@annotation(in.ineuron.annotation.ValidateRequestData) && args(requestData, result, ..)")
    public void validateUserBeforeMethodExecution(Object requestData, BindingResult result) {

        Map<String, String> errorResults = userUtils.getValidateUserCredentialError(result);
        if (!errorResults.isEmpty()) {
            throw new InvalidRequestDataException(
                    ErrorConstant.INVALID_REQUEST_FIELD_ERROR.getErrorCode(),
                    talkifyUtils.formatMap(errorResults),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
