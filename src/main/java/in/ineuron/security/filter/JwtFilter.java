package in.ineuron.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.ineuron.constant.ErrorConstant;
import in.ineuron.exception.TokenException;
import in.ineuron.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;
    private ObjectMapper objectMapper; // for writing JSON response

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            }

            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            chain.doFilter(request, response);

        } catch (SignatureException e) {
            handleException(response, ErrorConstant.TOKEN_INVALID_ERROR.getErrorCode(), ErrorConstant.TOKEN_INVALID_ERROR.getErrorMessage(), HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            handleException(response, ErrorConstant.TOKEN_EXPIRED_ERROR.getErrorCode(), ErrorConstant.TOKEN_EXPIRED_ERROR.getErrorMessage(), HttpStatus.UNAUTHORIZED);
        } catch (TokenException e) {
            handleException(response, e.getErrorCode(), e.getErrorMessage(), e.getStatus());
        }
    }

    private void handleException(HttpServletResponse response, int errorCode, String errorMessage, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        var errorResponse = Map.of(
                "errorCode", errorCode,
                "errorMessage", errorMessage
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
