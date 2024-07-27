package in.ineuron.services;

import in.ineuron.dto.*;
import in.ineuron.models.User;
import jakarta.servlet.http.Cookie;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public Boolean isUserAvailableByPhone(String phone);

    public Boolean isUserAvailableByEmail(String email);

    public Cookie getNewCookie(Long userId, String cookieName, int lifeTime);

    public User registerUser(RegisterRequest requestData);

    public User loginUser(LoginRequest loginData);

    public Integer generateOTP(String email);

    public boolean verifyOTP(VerifyOTPRequest request);

    public User updatePassword(UpdateUserPasswordReq request);

    public User fetchUserByPhone(String phone);

    public User fetchUserByEmail(String email);

    public User updateUserPassword(Long userId, String newPassword);

    public User findUserById(Long userId);

    public User fetchUserByAuthToken(String token);

    public List<User> searchUser(String query);

    public User updateUser(UserUpdateRequest userToUpdate);

    public Optional<User> fetchUserByUserid(String query);
}
