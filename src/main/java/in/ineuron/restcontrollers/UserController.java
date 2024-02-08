package in.ineuron.restcontrollers;

import in.ineuron.annotation.ValidateRequestData;
import in.ineuron.annotation.ValidateUser;
import in.ineuron.dto.*;
import in.ineuron.models.User;
import in.ineuron.services.OTPSenderService;
import in.ineuron.services.OTPStorageService;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    private final UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private OTPSenderService otpSender;

    @Autowired
    private OTPStorageService otpStorage;

    @Autowired
    private TokenStorageService tokenService;

    @Autowired
    private UserUtils userUtils;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint for registering a new user
    @PostMapping("/register")
    @ValidateRequestData
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest requestData, BindingResult result, HttpServletResponse response) {

        // Check if the email is already registered
        if (userService.isUserAvailableByEmail(requestData.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered with another account");
        }
        // Check if the phone number is already registered
        if (requestData.getPhone() != null && userService.isUserAvailableByPhone(requestData.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone No. already registered with another account");
        } else {
            User user = new User();
            BeanUtils.copyProperties(requestData, user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encrypt the user's password

            // Register the user in the system
            User regUser = userService.registerUser(user);

            String token = tokenService.generateToken(user.getId());
            Cookie cookie = new Cookie("auth-token", token);   //setting cookie
            int maxAge = 7 * 24 * 60 * 60;  // 7 days in seconds
            cookie.setMaxAge(maxAge);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(userUtils.getUserResponse(regUser));
        }
    }

    @ValidateRequestData
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginData, BindingResult result, HttpServletResponse response) {

        // Login using email
        User user = userService.fetchUserByEmail(loginData.getEmail());
        if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        } else {
            // Create a response object and set an authentication token cookie
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);

            String token = tokenService.generateToken(user.getId());
            Cookie cookie = new Cookie("auth-token", token);    //setting cookie
            int maxAge = 7 * 24 * 60 * 60;  // 7 days in seconds
            cookie.setMaxAge(maxAge);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);


            return ResponseEntity.ok(userResponse);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(@CookieValue("auth-token") String authToken, HttpServletResponse response) {
        tokenService.removeToken(authToken);
        Cookie cookie = new Cookie("auth-token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("User logged out successfully");
    }

    @GetMapping("/profile")
    @ValidateUser
    public ResponseEntity<UserResponse> getLoginUserDetails(@CookieValue("auth-token") String authToken, HttpServletRequest request) {

        Long id = tokenService.getUserIdFromToken(authToken);
        UserResponse userResponse = userUtils.getUserResponse(userService.findUserById(id));

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/check-login")
    public ResponseEntity<String> checkUserLogin(@CookieValue("auth-token") String authToken, HttpServletResponse response) {

        if (tokenService.isValidToken(authToken)) {
            return ResponseEntity.ok("true, User is logged in");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User login session has expired");
        }
    }


    @GetMapping("/send-otp")
    public ResponseEntity<String> sendOTPByPhone(@RequestParam("email") String email) throws MessagingException {

        if (userService.isUserAvailableByEmail(email)) {
            Integer OTP = null;
            OTP = otpSender.sendOTPByEmail(email);
            otpStorage.storeOTP(email, String.valueOf(OTP));

            return ResponseEntity.ok("Sent OTP: " + OTP);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found for " + email);
        }
    }

    @GetMapping("/verify-otp")
    public ResponseEntity<String> verifyOTPByPhone(
            @RequestParam("email") String email,
            @RequestParam String otp, HttpServletResponse response) throws MessagingException {

        if (!userService.isUserAvailableByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found for " + email);
        }

        if (otpStorage.verifyOTP(email, otp)) {
            otpStorage.removeOTP(email);

            //setting token for authorized user who wants to change password
            String token = tokenService.generateToken(null);
            Cookie cookie = new Cookie("otpVerified-token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            int maxAge = 5 * 60;  // 5 minutes in seconds
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);

            return ResponseEntity.ok("verified successfully.. ");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP verification failed.. ");
        }
    }

    @ValidateUser
    @PostMapping("/otp-verified/update-password")
    public ResponseEntity<?> UpdateUserPasswordAfterOTPVerified(@CookieValue("otpVerified-token") String authToken, @Valid @RequestBody UpdateUserPasswordDTO userCredential,
            BindingResult result) {

        User user = userService.fetchUserByEmail(userCredential.getEmail());
        userService.updateUserPassword(user.getId(), passwordEncoder.encode(userCredential.getNewPassword()));
        return ResponseEntity.ok("Password updated successfully..");
    }

    @ValidateUser
    @GetMapping("/search-users")
    public ResponseEntity<List<UserResponse>> searchUsersHandler(@CookieValue("auth-token") String authToken, @RequestParam String query) {

        if(query.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        }
        List<User> users = userService.searchUser(query);
        return ResponseEntity.status(HttpStatus.OK).body(userUtils.getUserResponse(users));
    }

    @PostMapping("update")
    @ValidateUser
    public ResponseEntity<UserResponse> updateUserHandler(@CookieValue("auth-token") String authToken,
                                                 @RequestBody UserUpdateRequest userToUpdate) {
        System.out.println(userToUpdate);
        User user = userService.updateUser(userToUpdate);
        return ResponseEntity.ok(userUtils.getUserResponse(user));
    }

    @GetMapping("/test-cookie")
    public ResponseEntity<String> someOtherEndpoint(@CookieValue("auth-token") String authToken) {

        if (tokenService.isValidToken(authToken)) {
            return ResponseEntity.ok("Valid token");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is expired");
        }
    }

}
