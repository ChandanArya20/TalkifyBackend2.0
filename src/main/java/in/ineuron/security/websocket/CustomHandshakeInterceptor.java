package in.ineuron.security.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

//@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                   org.springframework.web.socket.WebSocketHandler wsHandler, 
                                   Map<String, Object> attributes) throws Exception {
        // Extract Authorization header from the request
        String authorization = request.getHeaders().getFirst("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            attributes.put("token", token);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                               org.springframework.web.socket.WebSocketHandler wsHandler, 
                               Exception exception) {
        // Do nothing
    }
}
