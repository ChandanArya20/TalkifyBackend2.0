package in.ineuron.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.ineuron.constant.ErrorConstant;
import in.ineuron.dto.ErrorDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ErrorDetails error = new ErrorDetails();
        error.setErrorCode(ErrorConstant.NOT_ALLOWED_RESOURCE_ERROR.getErrorCode());
        error.setMessage(
                ErrorConstant.NOT_ALLOWED_RESOURCE_ERROR.getErrorMessage()+" : "+request.getServletPath());

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
