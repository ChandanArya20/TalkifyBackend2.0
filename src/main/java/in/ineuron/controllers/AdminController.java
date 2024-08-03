package in.ineuron.controllers;

import in.ineuron.dto.LoginRequest;
import in.ineuron.dto.UserResponse;
import in.ineuron.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("all-users")
    public List<UserResponse> fetchAllUsers(){
        return userService.fetchAllUsers();
    }

    @PostMapping
    public UserResponse createNewAdmin(@RequestBody LoginRequest userReq){
        return userService.makeUserAdmin(userReq);
    }

}
