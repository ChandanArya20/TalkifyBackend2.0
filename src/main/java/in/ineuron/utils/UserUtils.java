package in.ineuron.utils;

import in.ineuron.dto.UserResponse;
import in.ineuron.models.Role;
import in.ineuron.models.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

@Component
@AllArgsConstructor
public class UserUtils {

    // Method to convert User entity to UserResponse DTO
    public UserResponse getUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        List<String> roleList = user.getRoles().stream().map(Role::getName).toList();
//        userResponse.setRoles(roleList);
        return userResponse;
    }

    // Method to convert collection of User entities to list of UserResponse DTOs
    public List<UserResponse> getUserResponse(Collection<User> users) {
        List<UserResponse> userResponses = new ArrayList<>();

        for (User user : users) {
            UserResponse userResponse = getUserResponse(user);
            userResponses.add(userResponse);
        }
        return userResponses;
    }

}
