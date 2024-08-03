package in.ineuron.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.ineuron.constant.ErrorConstant;
import in.ineuron.dto.ErrorDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorDetails error = new ErrorDetails();
        error.setErrorCode(ErrorConstant.USER_UNAUTHORIZED_ERROR.getErrorCode());
        error.setMessage(ErrorConstant.USER_UNAUTHORIZED_ERROR.getErrorMessage());

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
