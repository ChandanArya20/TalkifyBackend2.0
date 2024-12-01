package in.ineuron.services;

import in.ineuron.dto.*;
import in.ineuron.models.User;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public Boolean isUserAvailableByPhone(String phone);

    public Boolean isUserAvailableByEmail(String email);

    public UserResponse saveUser(UserRequest requestData);

    public UserLoginResponse loginUser(LoginRequest loginData);

    String getUsername();

    public Integer generateAndSendOTP(String email);

    public boolean verifyOTP(VerifyOTPRequest request);

    public User updatePassword(UpdateUserPasswordReq request);

    public User fetchUserByPhone(String phone);

    public User fetchUserByEmail(String email);

    public User findUserById(String userId);

    public List<UserResponse> searchUser(String query);

    public UserResponse updateUser(UserUpdateRequest userToUpdate);

    public Optional<User> fetchUserByUserid(String query);

    List<UserResponse> fetchAllUsers();

    UserResponse makeUserAdmin(LoginRequest userReq);

    User getLoggedInUser();
}
