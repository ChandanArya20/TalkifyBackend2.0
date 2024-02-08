package in.ineuron.aspect;

import in.ineuron.exception.TokenException;
import in.ineuron.services.TokenStorageService;
import in.ineuron.utils.UserUtils;
;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;

@Aspect
@Component
public class UserValidationAspect {

    private final TokenStorageService tokenService;

    public UserValidationAspect(TokenStorageService tokenService) {
        this.tokenService = tokenService;
    }

    @Before("@annotation(in.ineuron.annotation.ValidateUser) && args(authToken, ..)")
    public void validateUserBeforeMethodExecution(@CookieValue("auth-token") String authToken) {
        System.out.println(authToken);
        if (!tokenService.isValidToken(authToken)) {
            throw new TokenException("Session is expired");
        }
    }

}
