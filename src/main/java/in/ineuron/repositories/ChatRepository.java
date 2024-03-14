package in.ineuron.repositories;

import in.ineuron.models.Chat;
import in.ineuron.models.User;
import in.ineuron.models.projection.ChatProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.isGroup=false AND :reqUserId MEMBER OF c.members AND :participantId MEMBER OF c.members")
    public Optional<ChatProjection> findSingleChatByUserIds(User reqUserId, User participantId );

    @Query("SELECT c FROM Chat c WHERE :user NOT MEMBER OF c.deletedByUsers AND :user MEMBER OF c.members")
    List<ChatProjection> findNonDeletedChatsByUser(User user);


}
