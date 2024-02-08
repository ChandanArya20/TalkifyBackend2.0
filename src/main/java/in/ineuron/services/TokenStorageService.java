package in.ineuron.services;

public interface TokenStorageService {

    String generateToken(Long userId);

    boolean isValidToken(String token);

    Long getUserIdFromToken(String token);

    void removeToken(String token);
}
