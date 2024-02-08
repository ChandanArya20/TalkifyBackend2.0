package in.ineuron.aspect;

import in.ineuron.dto.LoginRequest;
import in.ineuron.exception.InvalidRequestDataException;
import in.ineuron.utils.UserUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Aspect
@Component
public class RequestDataValidationAspect {

    private final UserUtils userUtils;

    public RequestDataValidationAspect(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @Before("@annotation(in.ineuron.annotation.ValidateRequestData) && args(requestData, result, ..)")
    public void validateUserBeforeMethodExecution(Object requestData, BindingResult result) {

        Map<String, String> errorResults = userUtils.validateUserCredential(result);
        if (!errorResults.isEmpty()) {
            throw new InvalidRequestDataException(errorResults);
        }
    }
}
