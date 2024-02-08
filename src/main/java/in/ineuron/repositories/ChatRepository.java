package in.ineuron.repositories;

import in.ineuron.models.Chat;
import in.ineuron.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.isGroup=false AND :reqUserId MEMBER OF c.members AND :participantId MEMBER OF c.members")
    public Optional<Chat> findSingleChatByUserIds(User reqUserId, User participantId );

    public List<Chat> findByMembersContaining(User user);


}
