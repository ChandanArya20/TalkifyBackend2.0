package in.ineuron.services;

import in.ineuron.dto.UserResponse;
import in.ineuron.dto.UserUpdateRequest;
import in.ineuron.exception.BadCredentialsException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.User;
import jakarta.servlet.http.Cookie;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public Boolean isUserAvailableByPhone(String phone);

    public Boolean isUserAvailableByEmail(String email);

    public Cookie getNewCookie(Long userId, String cookieName, int lifeTime);

    public User registerUser(User user);

    public User fetchUserByPhone(String phone);

    public User fetchUserByEmail(String email);

    public User updateUserPassword(Long userId, String newPassword);

    public User findUserById(Long userId);

    public User fetchUserByAuthToken(String token);

    public List<User> searchUser(String query);

    public User updateUser(UserUpdateRequest userToUpdate);

    public Optional<User> fetchUserByUserid(String query);
}
