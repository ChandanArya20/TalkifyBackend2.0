package in.ineuron.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Extract the necessary information for authentication from the headers
        // and put it in the attributes map.
        // For example, you can get the token from the request headers.
        // You may need to adjust this based on how your authentication is set up.

        // String authToken = request.getHeaders().getFirst("Authorization");
        // attributes.put("authToken", authToken);

        System.out.println("");
        System.out.println("From the HttpHandshakeInterceptor "+request.getHeaders());

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Nothing to do after handshake
    }
}
