//package in.ineuron.aspect;
//
//import in.ineuron.constant.ErrorConstant;
//import in.ineuron.exception.TokenException;
//import in.ineuron.services.TokenStorageService;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.CookieValue;
//
//@Aspect
//@Component
//public class UserValidationAspect {
//
//    private final TokenStorageService tokenService;
//
//    public UserValidationAspect(TokenStorageService tokenService) {
//        this.tokenService = tokenService;
//    }
//
//    @Before("@annotation(in.ineuron.annotation.ValidateUser) && args(authToken, ..)")
//    public void validateUserBeforeMethodExecution(@CookieValue("auth-token") String authToken) {
//
//        if (!tokenService.isValidToken(authToken)) {
//            throw new TokenException(
//                    ErrorConstant.TOKEN_EXPIRED_ERROR.getErrorCode(),
//                    ErrorConstant.TOKEN_EXPIRED_ERROR.getErrorMessage()+" : Session is expired",
//                    HttpStatus.UNAUTHORIZED
//            );
//        }
//    }
//
//}
