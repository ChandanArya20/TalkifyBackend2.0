package in.ineuron.services.impl;

import in.ineuron.services.TokenStorageService;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TokenStorageServiceImpl implements TokenStorageService {
    private static final long CLEANUP_INTERVAL_MINUTES = 1;
    private static final long TOKEN_EXPIRY_DURATION_MILLIS = 7 * 24 * 60 * 60 * 1000; // 7 days milliseconds
    private final Map<String, TokenInfo> tokenMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public TokenStorageServiceImpl() {
        // Schedule a cleanup task to remove expired tokens every 1 Hour.
        executorService.scheduleAtFixedRate(this::cleanUpExpiredTokens, CLEANUP_INTERVAL_MINUTES, CLEANUP_INTERVAL_MINUTES, TimeUnit.HOURS);
    }

    @Override
    public String generateToken(Long userId) {
        String token = UUID.randomUUID().toString();
        long creationTime = System.currentTimeMillis();
        TokenInfo tokenInfo = new TokenInfo(userId, creationTime);
        tokenMap.put(token, tokenInfo);
        // Log statement: System.out.println("Auth token generated: " + token);
        System.out.println(tokenMap);
        return token;
    }

    @Override
    public boolean isValidToken(String token) {
        TokenInfo tokenInfo = tokenMap.get(token);
        return tokenInfo != null && (System.currentTimeMillis() - tokenInfo.getCreationTime() <= TOKEN_EXPIRY_DURATION_MILLIS);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        TokenInfo tokenInfo = tokenMap.get(token);
        return (tokenInfo != null) ? tokenInfo.getUserId() : null;
    }

    @Override
    public void removeToken(String token) {
        tokenMap.remove(token);
    }

    private void cleanUpExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        // Iterate through the token map and remove entries older than the expiration duration.
        tokenMap.entrySet().removeIf(entry -> (currentTime - entry.getValue().getCreationTime() > TOKEN_EXPIRY_DURATION_MILLIS));
        // Log statement: System.out.println("Expired tokens cleaned up");
    }

    // Shutdown the executor service when the application stops
    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }

    @Getter
    private static class TokenInfo {
        private final Long userId;
        private final long creationTime;

        public TokenInfo(Long userId, long creationTime) {
            this.userId = userId;
            this.creationTime = creationTime;
        }

    }
}
