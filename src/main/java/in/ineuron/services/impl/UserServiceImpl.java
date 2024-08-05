package in.ineuron.services.impl;

import in.ineuron.constant.AuthRoles;
import in.ineuron.constant.ErrorConstant;
import in.ineuron.dto.*;
import in.ineuron.exception.EmailException;
import in.ineuron.exception.TokenException;
import in.ineuron.exception.UserException;
import in.ineuron.models.Role;
import in.ineuron.models.User;
import in.ineuron.repositories.UserRepository;
import in.ineuron.security.UserDetailsServiceImpl;
import in.ineuron.services.OTPSenderService;
import in.ineuron.services.OTPStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final OTPSenderService otpSender;
    private final OTPStorageService otpStorage;
    private final JwtUtil jwtUtil;

    @Value("${jwt.token.expiration}")
    private long tokenExpirationTime;


    @Override
    public Boolean isUserAvailableByPhone(String phone) {
        return userRepo.existsByPhone(phone);
    }

    @Override
    public Boolean isUserAvailableByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public User fetchUserByPhone(String phone) {
        return userRepo.findByPhone(phone).orElseThrow(
                () -> new UserException(
                        ErrorConstant.USER_NOT_FOUND_ERROR.getErrorCode(),
                        ErrorConstant.USER_NOT_FOUND_ERROR.getErrorMessage() + " by phone : " + phone,
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @Override
    public User fetchUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(
                () -> new UserException(
                        ErrorConstant.USER_NOT_FOUND_ERROR.getErrorCode(),
                        ErrorConstant.USER_NOT_FOUND_ERROR.getErrorMessage() + " by email : " + email,
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @Override
    public UserResponse saveUser(UserRequest requestData) {

        // Check if the email is already registered
        if (isUserAvailableByEmail(requestData.getEmail())) {
            throw new UserException(
                    ErrorConstant.USER_CONFLICT_ERROR.getErrorCode(),
                    ErrorConstant.USER_CONFLICT_ERROR.getErrorMessage() + " : Email already registered with another account",
                    HttpStatus.CONFLICT
            );
        }
        // Check if the phone number is already registered
        if (requestData.getPhone() != null && isUserAvailableByPhone(requestData.getPhone())) {
            throw new UserException(
                    ErrorConstant.USER_CONFLICT_ERROR.getErrorCode(),
                    ErrorConstant.USER_CONFLICT_ERROR.getErrorMessage() + " : Phone No. already registered with another account",
                    HttpStatus.CONFLICT
            );
        }
        // Check if the userid is already registered
        if (userRepo.findByUserid(requestData.getUserid()).isPresent()) {
            throw new UserException(
                    ErrorConstant.USER_CONFLICT_ERROR.getErrorCode(),
                    ErrorConstant.USER_CONFLICT_ERROR.getErrorMessage() + " : Userid not available",
                    HttpStatus.CONFLICT
            );
        }

        User user = new User();
        BeanUtils.copyProperties(requestData, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encrypt the user's password
        user.setActive(true);
        user.setRoles(Set.of(new Role(AuthRoles.USER)));
        // Register the user in the system
        user = userRepo.save(user);
        return mapToUserResponse(user);

    }

    @Override
    public UserLoginResponse loginUser(LoginRequest request) {

        try {
            User user = fetchUserByEmail(request.getEmail());
            String jwtToken = null;

            // Login using email
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword()));

            if (authenticate.isAuthenticated()) {
                jwtToken = jwtUtil.generateToken(request.getEmail(), tokenExpirationTime);
            }

            return new UserLoginResponse(jwtToken, mapToUserResponse(user));

        } catch (BadCredentialsException e) {
            throw new UserException(
                    ErrorConstant.INVALID_PASSWORD_ERROR.getErrorCode(),
                    ErrorConstant.INVALID_PASSWORD_ERROR.getErrorMessage(),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Override
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public Integer generateAndSendOTP(String email) {

        if (isUserAvailableByEmail(email)) {
            Integer OTP;

            try {
                OTP = otpSender.sendOTPByEmail(email);
            } catch (MessagingException e) {
                throw new EmailException(
                        ErrorConstant.EMAIL_SENDING_ERROR.getErrorCode(),
                        ErrorConstant.EMAIL_SENDING_ERROR.getErrorMessage() + " " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
            otpStorage.storeOTP(email, String.valueOf(OTP));

            return OTP;
        }

        throw new UserException(
                ErrorConstant.USER_NOT_FOUND_ERROR.getErrorCode(),
                ErrorConstant.USER_NOT_FOUND_ERROR.getErrorMessage() + " by email : " + email,
                HttpStatus.NOT_FOUND
        );
    }

    @Override
    public boolean verifyOTP(VerifyOTPRequest request) {
        if (!isUserAvailableByEmail(request.getEmail())) {
            throw new UserException(
                    ErrorConstant.USER_NOT_FOUND_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_FOUND_ERROR.getErrorMessage() + " by email : " + request.getEmail(),
                    HttpStatus.NOT_FOUND
            );
        }

        if (otpStorage.verifyOTP(request.getEmail(), request.getOTP())) {
            otpStorage.removeOTP(request.getEmail());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User updatePassword(UpdateUserPasswordReq request) {
        String newPassword = request.getNewPassword();
        User user = fetchUserByEmail(getUsername());
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPhone("9876543213");
        user.setUserid("ck_arya40");
        User save = userRepo.save(user);
        return save;
    }

    @Override
    public User findUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(
                () -> new UserException(
                        ErrorConstant.USER_NOT_FOUND_ERROR.getErrorCode(),
                        ErrorConstant.USER_NOT_FOUND_ERROR.getErrorMessage() + " by id : " + userId,
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @Override
    public List<UserResponse> searchUser(String query) {
        String email = getUsername();
        List<User> users = userRepo.searchUser(query);
        //Removes the logged user from list
        users=users.stream().filter(user-> !user.getEmail().equals(email)).toList();
        return mapToUserResponse(users);
    }

    @Override
    public Optional<User> fetchUserByUserid(String query) {
        return userRepo.findByUserid(query);
    }

    @Override
    public List<UserResponse> fetchAllUsers() {
        List<User> userList = userRepo.findAll();
        return mapToUserResponse(userList);
    }

    @Override
    public UserResponse makeUserAdmin(LoginRequest userReq) {
        User user = fetchUserByEmail(userReq.getEmail());
        if (user.getPassword().equals(userReq.getPassword())){
            throw new UserException(
                    ErrorConstant.INVALID_PASSWORD_ERROR.getErrorCode(),
                    ErrorConstant.INVALID_PASSWORD_ERROR.getErrorMessage()+" : user password is not current",
                    HttpStatus.UNAUTHORIZED
            );
        }
        Set<Role> roles = user.getRoles();
        roles.add(new Role(AuthRoles.ADMIN));
        user.setRoles(roles);
        User savedUser = userRepo.save(user);

        return mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest userToUpdate) {
        User user = fetchUserByEmail(getUsername());

        if (userToUpdate.getName() != null)
            user.setName(userToUpdate.getName());

        if (userToUpdate.getUserid() != null)
            user.setUserid(userToUpdate.getUserid());

        if (userToUpdate.getPhone() != null)
            user.setPhone(userToUpdate.getPhone());

        if (userToUpdate.getEmail() != null)
            user.setEmail(userToUpdate.getEmail());

        if (userToUpdate.getProfileImage() != null)
            user.setProfileImage(userToUpdate.getProfileImage());

        if (userToUpdate.getAbout() != null)
            user.setAbout(userToUpdate.getAbout());

        return mapToUserResponse(userRepo.save(user));
    }

    // Method to convert User entity to UserResponse DTO
    private UserResponse mapToUserResponse(User user){
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user,userResponse);
//        List<String> roleList = user.getRoles().stream().map(Role::getName).toList();
//        userResponse.setRoles(roleList);
        return userResponse;
    }

    // Method to convert collection of User entities to list of UserResponse DTOs
    private List<UserResponse> mapToUserResponse(Collection<User> users){
        List<UserResponse> userResponses = new ArrayList<>();

        for(User user:users){
            UserResponse userResponse = mapToUserResponse(user);
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public User getLoggedInUser(){
        return fetchUserByEmail(getUsername());
    }
}
