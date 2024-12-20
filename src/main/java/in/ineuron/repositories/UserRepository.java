package in.ineuron.repositories;

import in.ineuron.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    public boolean existsByPhone(String phone);

    public boolean existsByEmail(String email);

    public Optional<User> findByPhone(String phone);

    public Optional<User> findByEmail(String email);

    public Optional<User> findByUserid(String userid);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:query% OR u.userid LIKE %:query%")
    public List<User> searchUser(String query);

}
