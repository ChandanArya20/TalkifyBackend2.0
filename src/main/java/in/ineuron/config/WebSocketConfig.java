package in.ineuron.config;

import in.ineuron.security.websocket.CustomHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${frontend.app.baseURL}")
    private String frontendAppURL;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // These are endpoints the client can subscribe to.
        registry.enableSimpleBroker("/topic");
        // Message received with one of those below destinationPrefixes will be automatically router to controllers @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("websocket") // Handshake endpoint
                .setAllowedOrigins(frontendAppURL)
                .withSockJS();
    }
}
