package in.ineuron.utils;

import in.ineuron.dto.UserResponse;
import in.ineuron.exception.BadCredentialsException;
import in.ineuron.exception.TokenException;
import in.ineuron.models.User;
import in.ineuron.services.TokenStorageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

@Component
@AllArgsConstructor
public class UserUtils {

    private TokenStorageService tokenService;

    // Method to validate user credentials and return validation errors
    public Map<String, String> getValidateUserCredentialError(BindingResult result){

        Map<String, String> errorsMap = new HashMap<>();

        if (result.hasErrors()) {
            // Extract error messages and field names
            for (ObjectError error : result.getAllErrors()) {

                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    errorsMap.put(fieldError.getField(), error.getDefaultMessage());
                } else {
                    errorsMap.put("global", error.getDefaultMessage());
                }
            }
        }
        // Return only error messages and field names
        return errorsMap;
    }

    // Method to get OTP authentication token from request cookies
    public String getOTPAuthToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("otpVerified-token".equals(cookie.getName()) && !cookie.getValue().isBlank()) {
                    return cookie.getValue();
                }
            }
        }
        throw new TokenException("OTP-Token not found with request");
    }

    // Method to validate OTP authentication token
    public boolean validateOTPAuthToken(HttpServletRequest request) throws BadCredentialsException {

        String otpAuthToken = getOTPAuthToken(request);

        if(otpAuthToken==null){
            throw new BadCredentialsException("Token not found with request");
        }
        return tokenService.isValidToken(otpAuthToken);
    }

    // Method to convert User entity to UserResponse DTO
    public UserResponse getUserResponse(User user){

        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user,userResponse);

        return userResponse;
    }

    // Method to convert collection of User entities to list of UserResponse DTOs
    public List<UserResponse> getUserResponse(Collection<User> users){
        List<UserResponse> userResponses = new ArrayList<>();

        for(User user:users){
            UserResponse userResponse = getUserResponse(user);
            userResponses.add(userResponse);
        }
        return userResponses;
    }

}
