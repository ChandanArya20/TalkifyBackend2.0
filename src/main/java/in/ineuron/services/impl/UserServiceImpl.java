package in.ineuron.services.impl;

import in.ineuron.dto.UserUpdateRequest;
import in.ineuron.exception.BadCredentialsException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.User;
import in.ineuron.repositories.UserRepository;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Autowired
    private TokenStorageService tokenService;

    @Autowired
    private UserUtils userUtils;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Boolean isUserAvailableByPhone(String phone) {

        return userRepo.existsByPhone(phone);
    }

    @Override
    public Boolean isUserAvailableByEmail(String email) {

        return userRepo.existsByEmail(email);
    }

    @Override
    public User fetchUserByPhone(String phone){
        return userRepo.findByPhone(phone).orElseThrow(
                ()->new UserNotFoundException("User not found with phone "+phone)
        );
    }

    @Override
    public User fetchUserByEmail(String email){
        return userRepo.findByEmail(email).orElseThrow(
                ()->new UserNotFoundException("User not found with email "+email)
        );
    }

    @Override
    public User registerUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public User updateUserPassword(Long userId, String newPassword) {
        User user = findUserById(userId);
        user.setPassword(newPassword);
        return userRepo.save(user);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found with id " + userId)
        );
    }

    @Override
    public User fetchUserByAuthToken(String token) {
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId==null){
            throw new BadCredentialsException("Token expired...");
        }
        return findUserById(userId);
    }

    @Override
    public List<User> searchUser(String query) {
        return userRepo.searchUser(query);
    }

    public Optional<User> fetchUserByUserid(String query) {
        return userRepo.findByUserid(query);
    }

    @Override
    public User updateUser(UserUpdateRequest userToUpdate) {
        User user = findUserById(userToUpdate.getId());

        if(userToUpdate.getName()!=null)
            user.setName(userToUpdate.getName());

        if(userToUpdate.getPhone()!=null)
            user.setPhone(userToUpdate.getPhone());

        if(userToUpdate.getEmail()!=null)
            user.setEmail(userToUpdate.getEmail());

        if(userToUpdate.getProfileImage()!=null)
            user.setProfileImage(userToUpdate.getProfileImage());

        if(userToUpdate.getAbout()!=null)
            user.setAbout(userToUpdate.getAbout());

        System.out.println(user);
        return userRepo.save(user);
    }



}
