package in.ineuron.security;

import in.ineuron.constant.ErrorConstant;
import in.ineuron.exception.UserException;
import in.ineuron.models.Role;
import in.ineuron.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("UserDetailsServiceImpl.loadUserByUsername");
        in.ineuron.models.User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UserException(
                        ErrorConstant.USER_NOT_FOUND_ERROR.getErrorCode(),
                        ErrorConstant.USER_NOT_FOUND_ERROR.getErrorMessage() + " by id : " + username,
                        HttpStatus.NOT_FOUND
                ));

        String[] roles = null;
        if(user.getRoles()==null){
            roles = new String[0];
        }else {
            List<String> roleList = user.getRoles().stream().map(Role::getName).toList();
            roles = roleList.toArray(new String[0]);
        }

        return User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(roles)
                .build();
    }
}
