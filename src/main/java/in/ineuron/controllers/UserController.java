package in.ineuron.controllers;

import in.ineuron.constant.ErrorConstant;
import in.ineuron.dto.*;
import in.ineuron.exception.OTPException;
import in.ineuron.models.User;
import in.ineuron.services.UserService;
import in.ineuron.utils.JwtUtil;
import in.ineuron.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserUtils userUtils;
    private final JwtUtil jwtUtil;

    @Value("${jwt.token.otp.expiration}")
    private long otpTokenExpirationTime;

    // Endpoint for registering a new user
    @PostMapping("/signup")
    public ResponseEntity<UserLoginResponse> registerUser(@Valid @RequestBody UserRequest requestData) {
        UserResponse registeredUser = userService.saveUser(requestData);
        UserLoginResponse loginResponse = userService.loginUser(new LoginRequest(requestData.getEmail(), requestData.getPassword()));

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest loginData, HttpServletResponse response) {
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
    public ResponseEntity<String> checkUserLogin() {
        return ResponseEntity.ok("true, User is logged in");
    }

    @PostMapping("/otp/send")
    public ResponseEntity<String> sendOTPByPhone(@RequestParam("email") String email) throws MessagingException {
        Integer OTP = userService.generateAndSendOTP(email);
        return ResponseEntity.ok("Sent OTP: " + OTP);
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, String>> verifyOTPByPhone(@Valid @RequestBody VerifyOTPRequest request) throws MessagingException {
        if (userService.verifyOTP(request)) {
            //setting token for authorized user who wants to change password
            String token = jwtUtil.generateToken(request.getEmail(), otpTokenExpirationTime);
            var response = Map.of("message", "Verified successfully", "token", token);

            return ResponseEntity.ok(response);
        } else {
            throw new OTPException(
                    ErrorConstant.OTP_ERROR.getErrorCode(),
                    ErrorConstant.OTP_ERROR.getErrorMessage() + " : " + "OTP verification failed",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @PutMapping("password/update-with-otp")
    public ResponseEntity<UserLoginResponse> UpdateUserPasswordAfterOTPVerified(@Valid @RequestBody UpdateUserPasswordReq userCredential,
                                                                                        HttpServletResponse response) {
        User user = userService.updatePassword(userCredential);
        UserLoginResponse loginResponse = userService.loginUser(new LoginRequest(user.getEmail(), userCredential.getNewPassword()));

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/userid/available")
    public ResponseEntity<Boolean> checkUseridAvailability(@RequestParam String query) {
        if (query.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }
        Optional<User> userOptional = userService.fetchUserByUserid(query);

        return ResponseEntity.status(HttpStatus.OK).body(userOptional.isPresent());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsersHandler(@RequestParam String query) {
        if (query.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        }
        List<UserResponse> users = userService.searchUser(query);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PutMapping("update")
    public ResponseEntity<UserResponse> updateUserHandler(
            @RequestBody UserUpdateRequest userToUpdate) {
        //updates user data in db
        UserResponse user = userService.updateUser(userToUpdate);

        return ResponseEntity.ok(user);
    }

}
