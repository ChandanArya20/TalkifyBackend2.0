package in.ineuron.aspect;

import in.ineuron.exception.InvalidRequestDataException;
import in.ineuron.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Aspect
@Component
@AllArgsConstructor
public class RequestDataValidationAspect {

    private final UserUtils userUtils;

    @Before("@annotation(in.ineuron.annotation.ValidateRequestData) && args(requestData, result, ..)")
    public void validateUserBeforeMethodExecution(Object requestData, BindingResult result) {

        Map<String, String> errorResults = userUtils.getValidateUserCredentialError(result);
        if (!errorResults.isEmpty()) {
            throw new InvalidRequestDataException(errorResults);
        }
    }
}
