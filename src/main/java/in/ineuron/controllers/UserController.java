package in.ineuron.controllers;

import in.ineuron.annotation.ValidateRequestData;
import in.ineuron.annotation.ValidateUser;
import in.ineuron.constant.ErrorConstant;
import in.ineuron.dto.*;
import in.ineuron.exception.OTPException;
import in.ineuron.models.User;
import in.ineuron.services.OTPSenderService;
import in.ineuron.services.OTPStorageService;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static in.ineuron.constant.Constant.AUTH_TOKEN;
import static in.ineuron.constant.Constant.OTP_VERIFIED_TOKEN;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private BCryptPasswordEncoder passwordEncoder;
    private OTPSenderService otpSender;
    private OTPStorageService otpStorage;
    private TokenStorageService tokenService;
    private UserUtils userUtils;

    // Endpoint for registering a new user
    @PostMapping("/signup")
    @ValidateRequestData
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest requestData, BindingResult result) {

        UserResponse registeredUser = userService.saveUser(requestData);
        return ResponseEntity.ok(registeredUser);
    }

    @ValidateRequestData
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest loginData, BindingResult result, HttpServletResponse response) {

        UserLoginResponse user = userService.loginUser(loginData);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getLoginUserDetails() {

        String email = userService.getUsername();
        UserResponse userResponse = userUtils.getUserResponse(userService.fetchUserByEmail(email));

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/check-login")
    public ResponseEntity<String> checkUserLogin(@CookieValue(AUTH_TOKEN) String authToken, HttpServletResponse response) {

        if (tokenService.isValidToken(authToken)) {
            return ResponseEntity.ok("true, User is logged in");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User login session has expired");
        }
    }

    @PostMapping("/otp/send")
    public ResponseEntity<String> sendOTPByPhone(@RequestParam("email") String email) throws MessagingException {

        Integer OTP = userService.generateOTP(email);
        return ResponseEntity.ok("Sent OTP: " + OTP);
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<String> verifyOTPByPhone(@RequestBody VerifyOTPRequest request, HttpServletResponse response) throws MessagingException {

        if (userService.verifyOTP(request)){
            //setting token for authorized user who wants to change password
            response.addCookie(userService.getNewCookie(null,OTP_VERIFIED_TOKEN, 5 * 60)); // 5 minutes in seconds
            return ResponseEntity.ok("verified successfully.. ");
        } else {
            throw new OTPException(
                    ErrorConstant.OTP_ERROR.getErrorCode(),
                    ErrorConstant.OTP_ERROR.getErrorMessage()+" : "+"OTP verification failed",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @ValidateUser
    @PutMapping("password/update-with-otp")
    public ResponseEntity<UserResponse> UpdateUserPasswordAfterOTPVerified(@CookieValue(OTP_VERIFIED_TOKEN) String authToken, @Valid @RequestBody UpdateUserPasswordReq userCredential,
            BindingResult result, HttpServletResponse response) {

        User user = userService.updatePassword(userCredential);

        response.addCookie(userService.getNewCookie(user.getId(),AUTH_TOKEN, 7*24*60)); // 7 days lifetime for cookie
        return ResponseEntity.ok(userUtils.getUserResponse(user));
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkUseridAvailability(@RequestParam String query) {
        if(query.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }

        Optional<User> userOptional = userService.fetchUserByUserid(query);
        return ResponseEntity.status(HttpStatus.OK).body(userOptional.isPresent());
    }

    @ValidateUser
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsersHandler(@CookieValue(AUTH_TOKEN) String authToken, @RequestParam String query) {

        if(query.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        }

        User reqUser = userService.fetchUserByAuthToken(authToken);
        List<User> users = userService.searchUser(query);
        //Removes the logged user from list
        users=users.stream().filter(user-> user.getId() != reqUser.getId()).toList();
        return ResponseEntity.status(HttpStatus.OK).body(userUtils.getUserResponse(users));
    }

    @PutMapping("update")
    @ValidateUser
    public ResponseEntity<UserResponse> updateUserHandler(@CookieValue(AUTH_TOKEN) String authToken,
                                                 @RequestBody UserUpdateRequest userToUpdate) {

        Long userId = tokenService.getUserIdFromToken(authToken);
        userToUpdate.setId(userId);
        //updates user data in db
        User user = userService.updateUser(userToUpdate);
        return ResponseEntity.ok(userUtils.getUserResponse(user));
    }

}
